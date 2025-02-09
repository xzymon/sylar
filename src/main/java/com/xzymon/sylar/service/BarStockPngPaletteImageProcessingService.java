package com.xzymon.sylar.service;

import com.xzymon.sylar.constants.*;
import com.xzymon.sylar.constants.FontSize8Characters.FontSize8Character;
import com.xzymon.sylar.helper.DatesHelper;
import com.xzymon.sylar.helper.Helper;
import com.xzymon.sylar.model.*;
import com.xzymon.sylar.model.FrameCoords.FrameLine;
import com.xzymon.sylar.model.RawDataNipponCandle.BuildingBlock;
import com.xzymon.sylar.processing.ConfirmationGear;
import com.xzymon.sylar.processing.ImageExtractorServiceImpl;
import com.xzymon.sylar.processing.OptimizedAreaDetector;
import com.xzymon.sylar.processing.RawDataContainerToNipponCandlesConverter;
import io.nayuki.png.image.BufferedPaletteImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;

@Service
public class BarStockPngPaletteImageProcessingService implements StockPngPaletteImageProcessingService {
	private static final Logger LOGGER = LoggerFactory.getLogger(BarStockPngPaletteImageProcessingService.class);
	private static final String HEADER_LINE = "Date,Time,Open,High,Low,Close";

	public RawDataContainer extractRawDataFromImage(BufferedPaletteImage img) {
		FontSize8Characters fontSize8Characters = new FontSize8Characters();
		RawDataContainer result = new RawDataContainer();

		// get frame coords
		final int framePaletteColor = LayerPaletteColorOnImage.FRAMES;
		FrameCoords entireFC = new FrameCoords();
		ConfirmationGear confirmationGear = new ConfirmationGear(5, img.getHeight());

		detectVerticalFrameLineOnImage(img, FrameLine.LEFT, 0, img.getWidth(), 1, confirmationGear, framePaletteColor, entireFC);
		detectVerticalFrameLineOnImage(img, FrameLine.RIGHT, img.getWidth()-1, -1, -1, confirmationGear, framePaletteColor, entireFC);
		detectVerticalFrameLineExtremums(img, confirmationGear, entireFC, framePaletteColor);

		LOGGER.info("entireFC: {}", entireFC);
		FrameCoords timeAxisBarFC = detectTimeAxisBar(img, entireFC, LayerPaletteColorOnImage.TIME_BACKGROUND);
		if (timeAxisBarFC.getBottom() != null) {
			FrameCoords valueFC = new FrameCoords(entireFC);
			valueFC.setBottom(timeAxisBarFC.getTop() - 1);
			result.setValuesFrame(valueFC);
			LOGGER.info(String.format("valueFC : %1$s", valueFC));

			//wykryj linię zamknięcia z dnia poprzedniego
			result.setPreviousDayClose(detectPreviousDayClose(img, valueFC, LayerPaletteColorOnImage.PREVIOUS_DAY_CLOSE));

			//następnie trzeba będzie wydobywać prowadnice na wykresie wartości
			List<Integer> hGauges = detectReferencePointsForHorizontalGauges(img, valueFC, LayerPaletteColorOnImage.FRAMES);
			LOGGER.info(String.format("hGauges : %1$s", hGauges));
			Map<Integer, TextPixelArea> hGaugesMap = result.getHorizontalGauges();
			for (int gauge : hGauges) {
				FrameCoords aboutGaugeFC = new FrameCoords(gauge - FontSize8Characters.LINE_HEIGHT_HALF, img.getWidth()-1, gauge + FontSize8Characters.LINE_HEIGHT_HALF, valueFC.getRight()+1);
				TextPixelArea textPixelArea = ImageExtractorServiceImpl.exposeFromPixelArea(aboutGaugeFC, img, LayerPaletteColorOnImage.TEXT, LayerPaletteColorOnImage.BACKGROUND);
				textPixelArea.setExtractedText(detectTextInArea(aboutGaugeFC, img, LayerPaletteColorOnImage.TEXT, LayerPaletteColorOnImage.BACKGROUND, fontSize8Characters.getNumbersDotCharsSorted()));
				hGaugesMap.put(gauge, textPixelArea);
			}

			List<Integer> vGauges = detectReferencePointsForVerticalGauges(img, valueFC, LayerPaletteColorOnImage.FRAMES);
			LOGGER.info(String.format("vGauges : %1$s", vGauges));
			Map<Integer, TextPixelArea> vGaugesMap = result.getVerticalGauges();
			Map<String, Integer> textToVG = result.getTextToVG();
			for (int gauge : vGauges) {
				FrameCoords aboutGaugeFC = new FrameCoords(timeAxisBarFC.getTop(), gauge + FontSize8Characters.LETTER_WIDTH *3, timeAxisBarFC.getBottom(), gauge - FontSize8Characters.LETTER_WIDTH *3);
				TextPixelArea textPixelArea = ImageExtractorServiceImpl.exposeFromPixelArea(aboutGaugeFC, img, LayerPaletteColorOnImage.TEXT, LayerPaletteColorOnImage.TIME_BACKGROUND);
				String detectedText = detectTextInArea(aboutGaugeFC, img, LayerPaletteColorOnImage.TEXT, LayerPaletteColorOnImage.TIME_BACKGROUND, fontSize8Characters.getNumbersColonCharsSorted());
				textPixelArea.setExtractedText(detectedText);
				vGaugesMap.put(gauge, textPixelArea);
				if (detectedText != null && !detectedText.isEmpty() && !detectedText.trim().isEmpty()) {
					textToVG.put(detectedText, gauge);
				}
			}

			detectRawValuesFromArea(img, valueFC, result.getValueSeriesExtremalPoints(), result.getValuesPerHorizontal(), LayerPaletteColorOnImage.SERIES_LINE);

			if (timeAxisBarFC.getBottom() + 1 < entireFC.getBottom()) {
				FrameCoords volumeFC = new FrameCoords(entireFC);
				volumeFC.setTop(timeAxisBarFC.getBottom() + 1);
				result.setVolumeFrame(volumeFC);
				LOGGER.info(String.format("volumeFC : %1$s", volumeFC));
				detectRawValuesFromArea(img, volumeFC, result.getVolumeSeriesExtremalPoints(), result.getVolumePerHorizontal(), LayerPaletteColorOnImage.SERIES_LINE);
			}

			result.setValorNameArea(extractValorNameTextPixelArea(img, entireFC, result, fontSize8Characters));
			result.setGeneratedDateTimeArea(extractGeneratedDateTimeTextPixelArea(img, entireFC, result, fontSize8Characters));
			result.setIntervalArea(extractIntervalTextPixelArea(img, entireFC, result, img.getHeight(), fontSize8Characters));
		}

		if (result.getIntervalArea() != null && result.getIntervalArea().getExtractedText() != null && !result.getIntervalArea().getExtractedText().isEmpty()) {
			result.setChartType(ChartType.getChartType(result.getIntervalArea().getExtractedText().trim()));
			if (!ChartType.BAR.equals(result.getChartType())) {
				LOGGER.info(String.format("Incorrect chart type for service to process, chart type is: %1$s", result.getChartType()));
			}
		}

		if (result.getValuesPerHorizontal().size() > 0) {
			extractValuesInterpretationForHorizontalLines(result);
			extractBarChartValues(result);
		}

		return result;
	}

	private TextPixelArea extractIntervalTextPixelArea(BufferedPaletteImage img, FrameCoords entireFC, RawDataContainer result, int height, FontSize8Characters fontSize8Characters) {
		FrameCoords intervalAreaFC = new FrameCoords();
		intervalAreaFC.setLeft(getReferencePointFromHourTextOrRightProperty("20:00", entireFC, 5, 4, result));
		intervalAreaFC.setRight(entireFC.getRight());
		intervalAreaFC.setTop(entireFC.getBottom()+1);
		intervalAreaFC.setBottom(height -1);
		TextPixelArea intervalArea = ImageExtractorServiceImpl.exposeFromPixelArea(intervalAreaFC, img, LayerPaletteColorOnImage.TEXT, LayerPaletteColorOnImage.BACKGROUND);
		intervalArea.setExtractedText(detectTextInArea(intervalAreaFC, img, LayerPaletteColorOnImage.TEXT, LayerPaletteColorOnImage.BACKGROUND, fontSize8Characters.getAllKnownCharsNoWhitespaceSorted(), fontSize8Characters.getAllKnownCharsSorted()));
		return intervalArea;
	}

	private TextPixelArea extractValorNameTextPixelArea(BufferedPaletteImage img, FrameCoords entireFC, RawDataContainer rawDataContainer, FontSize8Characters fontSize8Characters) {
		FrameCoords fc = new FrameCoords();
		fc.setLeft(entireFC.getLeft());
		Integer right = Helper.getGaugeReferencePointByText("4:00", rawDataContainer.getVerticalGauges());
		if (right == null) {
			right = FontSize8Characters.LETTER_WIDTH *20;
		}
		fc.setRight(right);
		return extractTextPixelArea(img, entireFC, fontSize8Characters, fc);
	}

	private TextPixelArea extractGeneratedDateTimeTextPixelArea(BufferedPaletteImage img, FrameCoords entireFC, RawDataContainer rawDataContainer, FontSize8Characters fontSize8Characters) {
		FrameCoords fc = new FrameCoords();
		fc.setLeft(getReferencePointFromHourTextOrRightProperty("8:00", entireFC, 3,1, rawDataContainer));
		fc.setRight(getReferencePointFromHourTextOrRightProperty("16:00", entireFC, 3, 2, rawDataContainer));
		return extractTextPixelArea(img, entireFC, fontSize8Characters, fc);
	}

	private TextPixelArea extractTextPixelArea(BufferedPaletteImage img, FrameCoords entireFC, FontSize8Characters fontSize8Characters, FrameCoords fc) {
		fc.setTop(0);
		fc.setBottom(entireFC.getTop()-1);
		TextPixelArea result = ImageExtractorServiceImpl.exposeFromPixelArea(fc, img, LayerPaletteColorOnImage.TEXT, LayerPaletteColorOnImage.BACKGROUND);
		result.setExtractedText(detectTextInArea(fc, img, LayerPaletteColorOnImage.TEXT, LayerPaletteColorOnImage.BACKGROUND, fontSize8Characters.getAllKnownCharsNoWhitespaceSorted(), fontSize8Characters.getAllKnownCharsSorted()));
		return result;
	}

	private static Integer getReferencePointFromHourTextOrRightProperty(String hourText, FrameCoords entireFC, int divisor, int dividend, RawDataContainer rawDataContainer) {
		Integer result = Helper.getGaugeReferencePointByText(hourText, rawDataContainer.getHorizontalGauges());
		if (result == null) {
			result = entireFC.getRight() / divisor * dividend;
		}
		return result;
	}

	public CsvOutput toCsvOutput(RawDataContainer container) {
		CsvOutput result = new CsvOutput();
		List<String> csvRows = new ArrayList<>();
		result.setHeaderLine(HEADER_LINE);
		csvRows.add(result.getHeaderLine());

		NipponCandle previousDayCloseNipponCandle = RawDataContainerToNipponCandlesConverter.convertPreviousDayClose(container);
		result.setPreviousDayCloseLine(previousDayCloseNipponCandle.toCsvRow());
		csvRows.add(result.getPreviousDayCloseLine());

		//convert candle values to resulting time and value
		Map<Integer, NipponCandle> nipponCandlesMap = RawDataContainerToNipponCandlesConverter.convert(container);
		nipponCandlesMap.entrySet().stream()
				.map(entry -> entry.getValue().toCsvRow())
				.forEach(csvRows::add);
		result.setContent(csvRows);

		String trimmedValorName = container.getValorNameArea().getExtractedText().trim();
		String[] valorNameArr = trimmedValorName.split(" ");;
		String strictValorName = valorNameArr[0];
		String fileName = String.format("%1$s_b15m_%2$s_%3$s.csv",
				strictValorName,
				DatesHelper.getDateYYYYMMDD(container.getGeneratedDateTimeArea().getExtractedText()),
				"01"
		);
		result.setFileName(fileName);
		return result;
	}

	private void extractValuesInterpretationForHorizontalLines(RawDataContainer result) {
		Map<Integer, BigDecimal> valuesInterpretationMap = new HashMap<>();
		Map<Integer, TextPixelArea> horizontalGauges = result.getHorizontalGauges();
		for (Map.Entry<Integer, TextPixelArea> entry : horizontalGauges.entrySet()) {
			//System.out.println("pixel: " + entry.getKey() + " text: " + entry.getValue().getExtractedText());
			BigDecimal bigDecimal = new BigDecimal(entry.getValue().getExtractedText());
			//bigDecimal.doubleValue();
			valuesInterpretationMap.put(entry.getKey(), bigDecimal);
		}

		if (valuesInterpretationMap.size() > 1) {
			//interpolate range
			List<Integer> sortedRangePoints = new ArrayList<>(valuesInterpretationMap.keySet());
			Collections.sort(sortedRangePoints);
			int rangeGap, greaterPrecision, greatestPrecision = 0;
			double arithmeticOnePixelGap;
			Double lowestAOPixelGap = null, highestAOPixelGap = null;
			BigDecimal offset, subtracted, added;
			double currentValuesGap;
			Iterator<Integer> rangePointsIterator = sortedRangePoints.iterator();
			Integer lowestRangePoint, highestRangePoint = null, previousRangePoint, currentRangePoint;
			BigDecimal lowestRangeValue, highestRangeValue = null, previousRangeValue, currentRangeValue;
			lowestRangePoint = rangePointsIterator.next();
			lowestRangeValue = valuesInterpretationMap.get(lowestRangePoint);
			currentRangePoint = lowestRangePoint;
			currentRangeValue = lowestRangeValue;
			while (rangePointsIterator.hasNext()) {
				previousRangePoint = currentRangePoint;
				previousRangeValue = currentRangeValue;
				currentRangePoint = rangePointsIterator.next();
				currentRangeValue = valuesInterpretationMap.get(currentRangePoint);
				highestRangePoint = currentRangePoint;
				highestRangeValue = currentRangeValue;
				rangeGap = currentRangePoint - previousRangePoint;
				arithmeticOnePixelGap = (previousRangeValue.subtract(currentRangeValue)).doubleValue() / rangeGap;
				if (lowestAOPixelGap == null) lowestAOPixelGap = arithmeticOnePixelGap;
				highestAOPixelGap = arithmeticOnePixelGap;
				greaterPrecision = currentRangeValue.precision() > previousRangeValue.precision() ? currentRangeValue.precision() : previousRangeValue.precision();
				if (greaterPrecision > greatestPrecision) greatestPrecision = greaterPrecision;
				MathContext mc = new MathContext(greaterPrecision + 3);
				for (int loop = 1; loop < currentRangePoint - previousRangePoint; loop++) {
					offset = new BigDecimal(arithmeticOnePixelGap * loop);
					subtracted = previousRangeValue.subtract(offset);
					valuesInterpretationMap.put(previousRangePoint + loop, subtracted.round(mc));
				}
			}

			Integer firstRangePoint = sortedRangePoints.get(0);
			BigDecimal firstRangeValue = valuesInterpretationMap.get(firstRangePoint);
			lowestRangePoint = result.getValueSeriesExtremalPoints().getTop();
			if (lowestRangePoint < firstRangePoint) {
				rangeGap = firstRangePoint - lowestRangePoint;
				MathContext mc = new MathContext(greatestPrecision + 3);
				for (int loop = 1; loop <= rangeGap; loop++) {
					offset = new BigDecimal(lowestAOPixelGap * loop);
					added = firstRangeValue.add(offset);
					valuesInterpretationMap.put(firstRangePoint - loop, added.round(mc));
				}
			}

			Integer lastRangePoint = sortedRangePoints.get(sortedRangePoints.size()-1);
			BigDecimal lastRangeValue = valuesInterpretationMap.get(lastRangePoint);
			highestRangePoint = result.getValueSeriesExtremalPoints().getBottom();
			if (highestRangePoint != null && highestRangePoint > lastRangePoint) {
				rangeGap = highestRangePoint - lastRangePoint;
				MathContext mc = new MathContext(greatestPrecision + 3);
				for (int loop = 1; loop <= rangeGap; loop++) {
					offset = new BigDecimal(highestAOPixelGap * loop);
					subtracted = lastRangeValue.subtract(offset);
					valuesInterpretationMap.put(lastRangePoint + loop, subtracted.round(mc));
				}
			}
		}

		result.setHorizontalValuesMap(valuesInterpretationMap);
	}

	private void extractLineChartValues(RawDataContainer result) {

	}

	private void extractBarChartValues(RawDataContainer result) {
		List<RawDataNipponCandle> candles = result.getCandles();
		RawDataNipponCandle candle;
		int candlesInBucketCount = 0;
		int min=0, max=0;
		int expectedDayCandles = ChartType.BAR.getExpectedValuePointsCount();
		int leftMostX = result.getValueSeriesExtremalPoints().getLeft();
		int rightMostX = result.getValueSeriesExtremalPoints().getRight();
		int pixelsForAllCandles = rightMostX - leftMostX;
		//FIXME
		//int pixelsPerCandle = pixelsForAllCandles / expectedDayCandles;
		Integer verticalGauge2400 = result.getTextToVG().get(ChartType.BAR.getLastAlternativeTimePointText());
		Integer simulatedVerticalGauge0000 = result.getValuesFrame().getLeft();
		// +2 - bo tak jakby dodajemy swiece dla 00:00 i dla 24:00 - czyli 2 swiece
		int pixelsPerCandle = (verticalGauge2400 - simulatedVerticalGauge0000) / (ChartType.BAR.getExpectedValuePointsCount() + 2);
		List<BuildingBlock> buildingBlocks = new ArrayList<>();
		BuildingBlock buildingBlock;
		int candlePixelsCount, bucketXPosition;
		int buildingBlocksCount = 0;
		RawValueInBuckets currentBucket, previousBucket;
		RawValueInBuckets rawValueInBuckets=null;
		Iterator<RawValueInBuckets> candleMakerIt = result.getValuesPerHorizontal().iterator();
		while (candleMakerIt.hasNext()) {
			//get neighbouring buckets until reach pixelsPerCandle limit - to form each candle
			buildingBlock = new BuildingBlock();
			buildingBlocks.add(buildingBlock);
			buildingBlocksCount++;
			currentBucket = candleMakerIt.next();
			buildingBlock.getRawValues().add(currentBucket);
			candlePixelsCount = 1;
			bucketXPosition = currentBucket.getReferencePointOnAxis();
			while (candlePixelsCount < pixelsPerCandle) {
				if (candleMakerIt.hasNext()) {
					previousBucket = currentBucket;
					currentBucket = candleMakerIt.next();
					if (currentBucket.getReferencePointOnAxis() == bucketXPosition + 1) {
						candlePixelsCount++;
						bucketXPosition++;
						buildingBlock.getRawValues().add(currentBucket);
					} else {
						System.out.println(String.format("Bucket X position mismatch while in %1$d buildingBlock: %2$s != %3$s", buildingBlocksCount, bucketXPosition, currentBucket.getReferencePointOnAxis()));
						break;
					}
				}
			}
		}
		System.out.println(String.format("Building blocks count: %1$s", buildingBlocksCount));
		for (int i = 0; i < buildingBlocksCount; i++) {
			buildingBlock = buildingBlocks.get(i);
			candle = new RawDataNipponCandle();
			candlesInBucketCount = buildingBlock.getRawValues().size();
			Iterator<RawValueInBuckets> rawValueInBucketsIterator = buildingBlock.getRawValues().iterator();
			int count = 0;
			int referencePointsSum = 0;
			if (rawValueInBucketsIterator.hasNext()) {
				rawValueInBuckets = rawValueInBucketsIterator.next();
				candle.setOpen(rawValueInBuckets.getMin());             // nie ma znaczenia czy min czy max, oba powinny być takie same
				min = rawValueInBuckets.getMin();
				max = rawValueInBuckets.getMax();
				referencePointsSum += rawValueInBuckets.getReferencePointOnAxis();
				count++;
			}
			while (rawValueInBucketsIterator.hasNext()) {
				rawValueInBuckets = rawValueInBucketsIterator.next();
				if (rawValueInBuckets.getMin() < min) min = rawValueInBuckets.getMin();
				if (rawValueInBuckets.getMax() > max) max = rawValueInBuckets.getMax();
				referencePointsSum += rawValueInBuckets.getReferencePointOnAxis();
				count++;
			}
			if (rawValueInBuckets != null) {
				candle.setClose(rawValueInBuckets.getMax());            // nie ma znaczenia czy min czy max, oba powinny być takie same
			}
			candle.setHigh(min);    //to nie błąd, logika wartości w świecy jest inna niż logika liczenia pikseli
			candle.setLow(max);     //to nie błąd, logika wartości w świecy jest inna niż logika liczenia pikseli
			candle.setDatetimeMarker(referencePointsSum / count);
			candles.add(candle);
		}
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


	private void detectRawValuesFromArea(BufferedPaletteImage img, FrameCoords areaFC, FrameCoords extremumsFC, List<RawValueInBuckets> valuesInBuckets, int seriesLineLayerPci) {
		int hStart = areaFC.getLeft() + 1;
		int hEnd = areaFC.getRight();
		int vStart = areaFC.getTop() + 1;
		int vEnd = areaFC.getBottom();
		boolean valuePresent;
		RawValueInBuckets current = null;
		int series = vStart;

		//na potrzeby optymalizacji wydobywania wartości z wykresu
		boolean firstHValueRPDetected = false;      //flaga obecności jakiejkolwiek = pierwszej wartości z serii - na wykresie
		//punkty odniesienia pierwszej i ostatniej wartości na wykresie
		int leftMostX = hStart;
		int rightMostX = hStart;
		//punkty odniesienia extremów na wykresie
		int topMostY = vStart;   //położenie piksela będzie mieć wartość najmniejszą względem osi, ale z perspektywy znaczenia wartości na wykresie - będzie to wartość największa
		int bottomMostY = vEnd;  //odwrotnie niż powyżej

		// UWAGA: to co się tu dzieje jest nieintuicyjne
		// przeczesując obszar wykresu idziemy od lewej do prawej (to akurat intuicyjne)
		// ale pionowo idziemy od góry do dołu
		// - w ten sposób wartości w RawValueInBuckets są nadawane kontr-intuicyjnie,
		// tzn. wartość max położenia piksela jest wartością min na wykresie (bo tu wartości narastają z dołu do góry)
		for (int loopX = hStart; loopX < hEnd; loopX++) {
			valuePresent = false;
			//czesanie "w pionie" - z góry wykresu do dołu wykresu
			for (int loopY = vStart; loopY < vEnd; loopY++) {
				if (img.getPixel(loopX, loopY) == seriesLineLayerPci) {
					//znaleziono pierwszy piksel należący do (wycinka - paska) świecy
					valuePresent = true;
					current = new RawValueInBuckets(loopX, loopY, loopY);
					if (!firstHValueRPDetected) {
						firstHValueRPDetected = true;
						leftMostX = loopX;
						rightMostX = loopX;
						topMostY = loopY;
						bottomMostY = loopY;
					} else {
						if (loopX > rightMostX) rightMostX = loopX;
						if (loopY < topMostY) topMostY = loopY;
						if (loopY > bottomMostY) {
							bottomMostY = loopY;
							System.out.println(String.format("bottomMostY: %1$s", bottomMostY));
						}
					}
					valuesInBuckets.add(current);
					series = loopY + 1;
					break;
				}
			}
			// aktualizacja wartości w kubełku
			while (valuePresent) {
				if (img.getPixel(loopX, series) == seriesLineLayerPci) {
					current.setMax(series);
					if (series > bottomMostY) {
						bottomMostY = series;
						System.out.println(String.format("bottomMostY: %1$s", bottomMostY));
					}
					series++;
				} else {
					break;
				}
			}
			if (valuePresent) {
				LOGGER.info(String.format("f(%1$d)=(%2$d;%3$d)%n", current.getReferencePointOnAxis(), current.getMin(), current.getMax()));
			}
			if (firstHValueRPDetected) {
				extremumsFC.setLeft(leftMostX);
				extremumsFC.setRight(rightMostX);
				extremumsFC.setTop(topMostY);
				extremumsFC.setBottom(bottomMostY);
				LOGGER.info(String.format("extremumsFC : %1$s", extremumsFC));
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
				// zabezpieczenie na wypadek, gdyby od razu pod górną ramką była pierwsza horyzontalna linia - wtedy zafałszowywałaby zbierane wyniki
				if (img.getPixel(loopX, lineY+1) == frameColor) {
					LOGGER.info(String.format("Found gauge: %1$s", loopX));
					gauges.add(loopX);
				}
			}
		}
		return gauges;
	}

	private Integer detectPreviousDayClose(BufferedPaletteImage img, FrameCoords valueFC, int lineColor) {
		int firstInValueArea = valueFC.getLeft() + 1;
		int afterLastInValueArea = valueFC.getRight();
		for (int loopX = firstInValueArea; loopX < afterLastInValueArea; loopX++) {
			for (int lineY = valueFC.getTop() + 1; lineY < valueFC.getBottom(); lineY++) {
				if (img.getPixel(loopX, lineY) == lineColor) {
					LOGGER.info(String.format("Found previous day line: %1$s", lineY));
					return lineY;
				}
			}
		}
		return null;
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
