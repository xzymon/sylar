package com.xzymon.sylar.processing;

import com.xzymon.sylar.constants.FontSize8Characters;
import com.xzymon.sylar.constants.FontSize8Characters.FontSize8Character;
import com.xzymon.sylar.constants.LayerPaletteColorOnImage;
import com.xzymon.sylar.helper.Helper;
import com.xzymon.sylar.model.FrameCoords;
import com.xzymon.sylar.model.FrameCoords.FrameLine;
import com.xzymon.sylar.model.RawDataContainer;
import com.xzymon.sylar.model.RawValueInBuckets;
import com.xzymon.sylar.model.TextPixelArea;
import io.nayuki.png.image.BufferedPaletteImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StockPngPaletteImageProcessor {
	private static final Logger LOGGER = LoggerFactory.getLogger(StockPngPaletteImageProcessor.class);

	public RawDataContainer process(BufferedPaletteImage img) {
		FontSize8Characters fontSize8Characters = new FontSize8Characters();
		RawDataContainer result = new RawDataContainer();

		// get frame coords
		final int framePaletteColor = LayerPaletteColorOnImage.FRAMES;
		FrameCoords entireFC = new FrameCoords();
		FrameCoords valueFC = result.getValuesFrame();
		FrameCoords volumeFC = result.getVolumeFrame();
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
			LOGGER.info(String.format("valueFC : ", valueFC));

			//następnie trzeba będzie wydobywać prowadnice na wykresie wartości
			List<Integer> hGauges = detectReferencePointsForHorizontalGauges(img, valueFC, LayerPaletteColorOnImage.FRAMES);
			LOGGER.info(String.format("hGauges : %1$s", hGauges));
			Map<Integer, TextPixelArea> hGaugesMap = result.getHorizontalGauges();
			for (int gauge : hGauges) {
				FrameCoords aboutGaugeFC = new FrameCoords();
				aboutGaugeFC.setLeft(valueFC.getRight()+1);
				aboutGaugeFC.setRight(img.getWidth()-1);
				aboutGaugeFC.setTop(gauge - FontSize8Characters.LINE_HEIGHT_HALF);
				aboutGaugeFC.setBottom(gauge + FontSize8Characters.LINE_HEIGHT_HALF);
				TextPixelArea textPixelArea = ImageExtractorServiceImpl.exposeFromPixelArea(aboutGaugeFC, img, LayerPaletteColorOnImage.TEXT, LayerPaletteColorOnImage.BACKGROUND);
				textPixelArea.setExtractedText(detectTextInArea(aboutGaugeFC, img, LayerPaletteColorOnImage.TEXT, LayerPaletteColorOnImage.BACKGROUND, fontSize8Characters.getNumbersDotCharsSorted()));
				hGaugesMap.put(gauge, textPixelArea);
			}

			List<Integer> vGauges = detectReferencePointsForVerticalGauges(img, valueFC, LayerPaletteColorOnImage.FRAMES);
			LOGGER.info(String.format("vGauges : %1$s", vGauges));
			Map<Integer, TextPixelArea> vGaugesMap = result.getVerticalGauges();
			for (int gauge : vGauges) {
				FrameCoords aboutGaugeFC = new FrameCoords();

				aboutGaugeFC.setTop(timeAxisBarFC.getTop());
				aboutGaugeFC.setRight(gauge + FontSize8Characters.LETTER_WIDTH *3);
				aboutGaugeFC.setBottom(timeAxisBarFC.getBottom());
				aboutGaugeFC.setLeft(gauge - FontSize8Characters.LETTER_WIDTH *3);

				TextPixelArea textPixelArea = ImageExtractorServiceImpl.exposeFromPixelArea(aboutGaugeFC, img, LayerPaletteColorOnImage.TEXT, LayerPaletteColorOnImage.TIME_BACKGROUND);
				textPixelArea.setExtractedText(detectTextInArea(aboutGaugeFC, img, LayerPaletteColorOnImage.TEXT, LayerPaletteColorOnImage.TIME_BACKGROUND, fontSize8Characters.getNumbersColonCharsSorted()));
				vGaugesMap.put(gauge, textPixelArea);
			}

			detectRawValuesFromArea(img, valueFC, result.getValuesPerHorizontal(), LayerPaletteColorOnImage.SERIES_LINE);

			if (timeAxisBarFC.getBottom() + 1 < entireFC.getBottom()) {
				volumeFC.setLeft(entireFC.getLeft());
				volumeFC.setRight(entireFC.getRight());
				volumeFC.setTop(timeAxisBarFC.getBottom() + 1);
				volumeFC.setBottom(entireFC.getBottom());
				LOGGER.info(String.format("volumeFC : %1$s", volumeFC));
				detectRawValuesFromArea(img, volumeFC, result.getVolumePerHorizontal(), LayerPaletteColorOnImage.SERIES_LINE);
			}

			//detect other texts
			//valorName area
			FrameCoords valorNameFC = new FrameCoords();
			valorNameFC.setLeft(entireFC.getLeft());
			Integer right = Helper.getGaugeReferencePointByText("4:00", result.getVerticalGauges());
			if (right == null) {
				right = FontSize8Characters.LETTER_WIDTH *20;
			}
			valorNameFC.setRight(right);
			valorNameFC.setTop(0);
			valorNameFC.setBottom(entireFC.getTop()-1);
			TextPixelArea valorNameArea = ImageExtractorServiceImpl.exposeFromPixelArea(valorNameFC, img, LayerPaletteColorOnImage.TEXT, LayerPaletteColorOnImage.BACKGROUND);
			valorNameArea.setExtractedText(detectTextInArea(valorNameFC, img, LayerPaletteColorOnImage.TEXT, LayerPaletteColorOnImage.BACKGROUND, fontSize8Characters.getAllKnownCharsNoWhitespaceSorted(), fontSize8Characters.getAllKnownCharsSorted()));
			result.setValorNameArea(valorNameArea);

			//generatedDateTimeArea
			FrameCoords generatedDateTimeAreaFC = new FrameCoords();
			Integer leftDT = Helper.getGaugeReferencePointByText("8:00", result.getHorizontalGauges());
			if (leftDT == null) {
				leftDT = entireFC.getRight() / 3;
			}
			generatedDateTimeAreaFC.setLeft(leftDT);
			Integer rightDT = Helper.getGaugeReferencePointByText("16:00", result.getHorizontalGauges());
			if (rightDT == null) {
				rightDT = (entireFC.getRight() / 3) * 2;
			}
			generatedDateTimeAreaFC.setRight(rightDT);
			generatedDateTimeAreaFC.setTop(0);
			generatedDateTimeAreaFC.setBottom(entireFC.getTop()-1);
			TextPixelArea generatedDateTimeArea = ImageExtractorServiceImpl.exposeFromPixelArea(generatedDateTimeAreaFC, img, LayerPaletteColorOnImage.TEXT, LayerPaletteColorOnImage.BACKGROUND);
			generatedDateTimeArea.setExtractedText(detectTextInArea(generatedDateTimeAreaFC, img, LayerPaletteColorOnImage.TEXT, LayerPaletteColorOnImage.BACKGROUND, fontSize8Characters.getAllKnownCharsNoWhitespaceSorted(), fontSize8Characters.getAllKnownCharsSorted()));
			result.setGeneratedDateTimeArea(generatedDateTimeArea);

			//intervalArea
			FrameCoords intervalAreaFC = new FrameCoords();
			Integer leftI = Helper.getGaugeReferencePointByText("20:00", result.getHorizontalGauges());
			if (leftI == null) {
				leftI = (entireFC.getRight() / 5) * 4;
			}
			intervalAreaFC.setLeft(leftI);
			intervalAreaFC.setRight(entireFC.getRight());
			intervalAreaFC.setTop(entireFC.getBottom()+1);
			intervalAreaFC.setBottom(height-1);
			TextPixelArea intervalArea = ImageExtractorServiceImpl.exposeFromPixelArea(intervalAreaFC, img, LayerPaletteColorOnImage.TEXT, LayerPaletteColorOnImage.BACKGROUND);
			intervalArea.setExtractedText(detectTextInArea(intervalAreaFC, img, LayerPaletteColorOnImage.TEXT, LayerPaletteColorOnImage.BACKGROUND, fontSize8Characters.getAllKnownCharsNoWhitespaceSorted(), fontSize8Characters.getAllKnownCharsSorted()));
			result.setIntervalArea(intervalArea);
		}
		return result;
	}

	private String detectTextInArea(FrameCoords area, BufferedPaletteImage img, int paletteColorToSense, int clearValue, List<FontSize8Character> charsToDetect) {
		OptimizedAreaDetector optimizedAreaDetector = new OptimizedAreaDetector(area, img, paletteColorToSense, charsToDetect, clearValue);
		String detectedChars = optimizedAreaDetector.detect();
		System.out.println(String.format("Detected chars: %1$s", detectedChars));
		return detectedChars;
	}

	private String detectTextInArea(FrameCoords area, BufferedPaletteImage img, int paletteColorToSense, int clearValue, List<FontSize8Character> charsToDetect, List<FontSize8Character> charsToDetectWhenNotEmpty) {
		OptimizedAreaDetector optimizedAreaDetector = new OptimizedAreaDetector(area, img, paletteColorToSense, charsToDetect, charsToDetectWhenNotEmpty, clearValue);
		String detectedChars = optimizedAreaDetector.detect();
		System.out.println(String.format("Detected chars: %1$s", detectedChars));
		return detectedChars;
	}


	private void detectRawValuesFromArea(BufferedPaletteImage img, FrameCoords areaFC, List<RawValueInBuckets> valuesInBuckets, int seriesLineLayerPci) {
		int hStart = areaFC.getLeft() + 1;
		int hEnd = areaFC.getRight();
		int vStart = areaFC.getTop() + 1;
		int vEnd = areaFC.getBottom();
		boolean valuePresent;
		RawValueInBuckets current = null;
		int series = vStart;
		for (int loopX = hStart; loopX < hEnd; loopX++) {
			valuePresent = false;
			for (int loopY = vStart; loopY < vEnd; loopY++) {
				if (img.getPixel(loopX, loopY) == seriesLineLayerPci) {
					valuePresent = true;
					current = new RawValueInBuckets(loopX, loopY, loopY);
					valuesInBuckets.add(current);
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
				LOGGER.info(String.format("f(%1$d)=(%2$d;%3$d)%n", current.getHorizontal(), current.getMin(), current.getMax()));
			}
		}
	}

	private List<Integer> detectReferencePointsForHorizontalGauges(BufferedPaletteImage img, FrameCoords valueFC, int frameColor) {
		int firstInValueArea = valueFC.getTop() + 1;
		int afterLastInValueArea = valueFC.getBottom();
		int lineX = valueFC.getLeft() + 1;
		List<Integer> gauges = new ArrayList<>();
		for (int loopY = firstInValueArea; loopY < afterLastInValueArea; loopY++) {
			if (img.getPixel(lineX, loopY) == frameColor) {
				LOGGER.info(String.format("Found gauge: %1$s", loopY));
				gauges.add(loopY);
			}
		}
		return gauges;
	}

	private List<Integer> detectReferencePointsForVerticalGauges(BufferedPaletteImage img, FrameCoords valueFC, int frameColor) {
		int firstInValueArea = valueFC.getLeft() + 1;
		int afterLastInValueArea = valueFC.getRight();
		int lineY = valueFC.getTop() + 1;
		List<Integer> gauges = new ArrayList<>();
		for (int loopX = firstInValueArea; loopX < afterLastInValueArea; loopX++) {
			if (img.getPixel(loopX, lineY) == frameColor) {
				LOGGER.info(String.format("Found gauge: %1$s", loopX));
				gauges.add(loopX);
			}
		}
		return gauges;
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
			LOGGER.info(String.format("Entire Frame Coords : %1$s", result));
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
			LOGGER.info(String.format("[%1$d] : ", loopX));
			for (int loopArr = 0; loopArr < confirmationLevel; loopArr++) {
				pixelPaletteColor = img.getPixel(loopX, confirmYArr[loopArr]);
				LOGGER.info(String.format("[%1$d] = %2$d, ", loopX, pixelPaletteColor));
				if (pixelPaletteColor == paletteColorToSense) {
					hits++;
				}
			}
			if (hits == confirmationLevel) {
				lineFoundConfirmationAndSet(frameLine, loopX, valueFC);
				break;
			}
		}
	}

	private static void lineFoundConfirmationAndSet(FrameLine frameLine, int value, FrameCoords frameCoords) {
		LOGGER.info(String.format("%1$s line found at %2$d%n", frameLine, value));
		switch (frameLine) {
			case LEFT -> frameCoords.setLeft(value);
			case TOP -> frameCoords.setTop(value);
			case RIGHT -> frameCoords.setRight(value);
			case BOTTOM -> frameCoords.setBottom(value);
		}
	}
}
