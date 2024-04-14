package com.xzymon.sylar.json;

import com.xzymon.sylar.constants.LayerPaletteColorOnImage;
import com.xzymon.sylar.json.FrameCoords.FrameLine;
import com.xzymon.sylar.processing.ConfirmationGear;
import com.xzymon.sylar.processing.TextPixelArea;
import com.xzymon.sylar.processing.VerticalLineBucket;
import io.nayuki.png.image.BufferedPaletteImage;

import java.util.ArrayList;
import java.util.List;

public class ExtractedData {
	private FrameCoords valuesFrame;                    //granice wykresu notowań
	private FrameCoords volumeFrame;                    //granice wykresu obrotów
	private List<RawValueInBuckets> valuesPerHorizontal;//notowania (liczone kubełkmi)
	private List<RawValueInBuckets> volumePerHorizontal;//obroty (liczone kubełkami)
	private List<VerticalLineBucket> valueVLBuckets;    //kubełki (odpowiadające liniom) dla notowań
	private Integer dayOpeningBucket;                   //linia kubełka wartości otwarcia dnia
	private Integer dayMaxBucket;                       //linia kubełka wartości max z dnia
	private Integer dayMinBucket;                       //linia kubełka wartości min z dnia
	private TextPixelArea valorNameArea;                //nazwa waloru - obszar obrazu do ekstrakcji tekstu
	private TextPixelArea generatedDateTimeArea;        //data i czas publikacji danych wykresu w tej postaci - obszar obrazu do ekstrakcji tekstu
	private List<TextPixelArea> horizontalGaugesAreas;  //lista dla prowadnic poziomych - obszar obrazu do ekstrakcji tekstu
	private List<TextPixelArea> verticalGaugesAreas;    //lista dla prowadnic pionowych - obszar obrazu do ekstrakcji tekstu
	private TextPixelArea intervalArea;                 //interval - obszar obrazu do ekstrakcji tekstu

	public void extractData(BufferedPaletteImage img) {
		// get frame coords
		final int framePaletteColor = LayerPaletteColorOnImage.FRAMES;
		FrameCoords entireFC = new FrameCoords();
		FrameCoords valueFC = new FrameCoords();
		FrameCoords volumeFC = new FrameCoords();
		int confirmationLevel = 5;
		int height = img.getHeight();
		int width = img.getWidth();
		ConfirmationGear confirmationGear = new ConfirmationGear(confirmationLevel, height);
		// search for left edge frame
		detectVerticalFrameLineOnImage(img, FrameLine.LEFT, 0, width, 1, confirmationGear, framePaletteColor, entireFC);
		// search for right edge of frame
		detectVerticalFrameLineOnImage(img, FrameLine.RIGHT, width-1, -1, -1, confirmationGear, framePaletteColor, entireFC);
		detectVerticalFrameLineExtremums(img, confirmationGear, entireFC, framePaletteColor);
		System.out.println(entireFC);
		FrameCoords timeAxisBarFC = detectTimeAxisBar(img, entireFC, LayerPaletteColorOnImage.TIME_BACKGROUND);
		if (timeAxisBarFC.getBottom() != null) {
			valueFC.setLeft(entireFC.getLeft());
			valueFC.setRight(entireFC.getRight());
			valueFC.setTop(entireFC.getTop());
			valueFC.setBottom(timeAxisBarFC.getTop() - 1);
			System.out.println("valueFC : " + valueFC);

			volumeFC.setLeft(entireFC.getLeft());
			volumeFC.setRight(entireFC.getRight());
			volumeFC.setTop(timeAxisBarFC.getBottom() + 1);
			volumeFC.setBottom(entireFC.getBottom());
			System.out.println("volumeFC : " + volumeFC);

			//następnie trzeba będzie wydobywać prowadnice na wykresie wartości
			detectHorizontalGauges(img, valueFC, LayerPaletteColorOnImage.FRAMES);
			List<RawValueInBuckets> rawValuesListInBuckets = detectRawValues(img, valueFC, LayerPaletteColorOnImage.SERIES_LINE);
		}
	}

	private List<RawValueInBuckets> detectRawValues(BufferedPaletteImage img, FrameCoords valueFC, int seriesLineLayerPci) {
		List<RawValueInBuckets> result = new ArrayList<>();
		int hStart = valueFC.getLeft() + 1;
		int hEnd = valueFC.getRight();
		int vStart = valueFC.getTop() + 1;
		int vEnd = valueFC.getBottom();
		boolean valuePresent;
		RawValueInBuckets current = null;
		int series = vStart;
		for (int loopX = hStart; loopX < hEnd; loopX++) {
			valuePresent = false;
			for (int loopY = vStart; loopY < vEnd; loopY++) {
				if (img.getPixel(loopX, loopY) == seriesLineLayerPci) {
					valuePresent = true;
					current = new RawValueInBuckets(loopX, loopY, loopY);
					result.add(current);
					series = loopY + 1;
					break;
				}
			}
			while (valuePresent) {
				if (img.getPixel(loopX, series) == seriesLineLayerPci) {
					current.setMax(series);
					series++;
				} else {
					break;
				}
			}
			if (valuePresent) {
				System.out.format("f(%1$d)=(%2$d;%3$d)%n", current.getHorizontal(), current.getMin(), current.getMax());
			}
		}
		return result;
	}

	private void detectHorizontalGauges(BufferedPaletteImage img, FrameCoords valueFC, int frameColor) {
		int firstInValueArea = valueFC.getTop() + 1;
		int afterLastInValueArea = valueFC.getBottom();
		int lineX = valueFC.getLeft() + 1;
		List<Integer> gauges = new ArrayList<>();
		for (int loopY = firstInValueArea; loopY < afterLastInValueArea; loopY++) {
			if (img.getPixel(lineX, loopY) == frameColor) {
				System.out.println("Found gauge: " + loopY);
				gauges.add(loopY);
			}
		}
	}

	private FrameCoords detectTimeAxisBar(BufferedPaletteImage img, FrameCoords entireFC, int timeAxisBarColor) {
		FrameCoords result = new FrameCoords();
		//podążanie od brzegów entireFC do środka
		int lineMainCoord = entireFC.getLeft();
		int minY = entireFC.getTop();
		int maxY = entireFC.getBottom();

		// first to the right from left edge of entireFC
		int leftEdgeOfTimeAxisBar = lineMainCoord + 1;
		//descending from maxY to maxY of timeAxisBar (which, on the image is the bottom of timeAxisBar)
		int descendingCursor = maxY-1;
		while (descendingCursor > minY && img.getPixel(leftEdgeOfTimeAxisBar, descendingCursor) != timeAxisBarColor) {
			descendingCursor--;
		}
		// jezeli znaleziono obszar timeAxisBar - jezeli znaleziono piksel o kolorze odpowiadającym temu obszarowi
		if (descendingCursor > minY) {
			result.setBottom(descendingCursor);
			int ascendingCursor = minY+1;
			while (ascendingCursor < maxY && img.getPixel(leftEdgeOfTimeAxisBar, ascendingCursor) != timeAxisBarColor) {
				ascendingCursor++;
			}
			if (ascendingCursor < maxY) {
				result.setTop(ascendingCursor);
			}
			System.out.println("result : " + result);
		}
		return result;
	}

	private static void detectVerticalFrameLineExtremums(BufferedPaletteImage img, ConfirmationGear confirmationGear, FrameCoords frameCoords, int paletteColorToSense) {
		int[] levelsArray = confirmationGear.getLevelPoints();
		int levelsArrayLen = levelsArray.length;
		int minStart = levelsArray[0];
		int maxStart = levelsArray[levelsArrayLen-1];

		int lineMainCoord = frameCoords.getLeft();
		int descendingCursor = minStart;
		int levelColor = img.getPixel(lineMainCoord, descendingCursor);
		while (img.getPixel(lineMainCoord, descendingCursor-1) == levelColor) {
			descendingCursor--;
		}
		frameCoords.setTop(descendingCursor);

		int ascendingCursor = maxStart;
		levelColor = img.getPixel(lineMainCoord, ascendingCursor);
		while (img.getPixel(lineMainCoord, ascendingCursor+1) == levelColor) {
			ascendingCursor++;
		}
		frameCoords.setBottom(ascendingCursor);
	}

	private static void detectVerticalFrameLineOnImage(BufferedPaletteImage img, FrameLine frameLine, int loopStart, int loopEnd, int loopStep, ConfirmationGear confirmationGear, int paletteColorToSense, FrameCoords valueFC) {
		int pixelPaletteColor;
		int hits;
		int confirmationLevel = confirmationGear.getLevel();
		int[] confirmYArr = confirmationGear.getLevelPoints();
		for (int loopX = loopStart; loopX != loopEnd; loopX=loopX + loopStep) {
			hits = 0;
			System.out.printf("[%1$d] : ", loopX);
			for (int loopArr = 0; loopArr < confirmationLevel; loopArr++) {
				pixelPaletteColor = img.getPixel(loopX, confirmYArr[loopArr]);
				System.out.printf("[%1$d] = %2$d, ", loopX, pixelPaletteColor);
				if (pixelPaletteColor == paletteColorToSense) {
					hits++;
				}
			}
			System.out.println();
			if (hits == confirmationLevel) {
				lineFoundConfirmationAndSet(frameLine, loopX, valueFC);
				break;
			}
		}
	}

	private static void lineFoundConfirmationAndSet(FrameLine frameLine, int value, FrameCoords frameCoords) {
		System.out.format("%1$s line found at %2$d%n", frameLine, value);
		switch (frameLine) {
			case LEFT -> frameCoords.setLeft(value);
			case TOP -> frameCoords.setTop(value);
			case RIGHT -> frameCoords.setRight(value);
			case BOTTOM -> frameCoords.setBottom(value);
		}
	}

	public FrameCoords getValuesFrame() {
		return valuesFrame;
	}

	public void setValuesFrame(FrameCoords valuesFrame) {
		this.valuesFrame = valuesFrame;
	}

	public FrameCoords getVolumeFrame() {
		return volumeFrame;
	}

	public void setVolumeFrame(FrameCoords volumeFrame) {
		this.volumeFrame = volumeFrame;
	}

	public List<RawValueInBuckets> getValuesPerHorizontal() {
		return valuesPerHorizontal;
	}

	public void setValuesPerHorizontal(List<RawValueInBuckets> valuesPerHorizontal) {
		this.valuesPerHorizontal = valuesPerHorizontal;
	}

	public List<RawValueInBuckets> getVolumePerHorizontal() {
		return volumePerHorizontal;
	}

	public void setVolumePerHorizontal(List<RawValueInBuckets> volumePerHorizontal) {
		this.volumePerHorizontal = volumePerHorizontal;
	}

	public List<VerticalLineBucket> getValueVLBuckets() {
		return valueVLBuckets;
	}

	public void setValueVLBuckets(List<VerticalLineBucket> valueVLBuckets) {
		this.valueVLBuckets = valueVLBuckets;
	}

	public Integer getDayOpeningBucket() {
		return dayOpeningBucket;
	}

	public void setDayOpeningBucket(Integer dayOpeningBucket) {
		this.dayOpeningBucket = dayOpeningBucket;
	}

	public Integer getDayMaxBucket() {
		return dayMaxBucket;
	}

	public void setDayMaxBucket(Integer dayMaxBucket) {
		this.dayMaxBucket = dayMaxBucket;
	}

	public Integer getDayMinBucket() {
		return dayMinBucket;
	}

	public void setDayMinBucket(Integer dayMinBucket) {
		this.dayMinBucket = dayMinBucket;
	}

	public TextPixelArea getValorNameArea() {
		return valorNameArea;
	}

	public void setValorNameArea(TextPixelArea valorNameArea) {
		this.valorNameArea = valorNameArea;
	}

	public TextPixelArea getGeneratedDateTimeArea() {
		return generatedDateTimeArea;
	}

	public void setGeneratedDateTimeArea(TextPixelArea generatedDateTimeArea) {
		this.generatedDateTimeArea = generatedDateTimeArea;
	}

	public List<TextPixelArea> getHorizontalGaugesAreas() {
		return horizontalGaugesAreas;
	}

	public void setHorizontalGaugesAreas(List<TextPixelArea> horizontalGaugesAreas) {
		this.horizontalGaugesAreas = horizontalGaugesAreas;
	}

	public List<TextPixelArea> getVerticalGaugesAreas() {
		return verticalGaugesAreas;
	}

	public void setVerticalGaugesAreas(List<TextPixelArea> verticalGaugesAreas) {
		this.verticalGaugesAreas = verticalGaugesAreas;
	}

	public TextPixelArea getIntervalArea() {
		return intervalArea;
	}

	public void setIntervalArea(TextPixelArea intervalArea) {
		this.intervalArea = intervalArea;
	}
}
