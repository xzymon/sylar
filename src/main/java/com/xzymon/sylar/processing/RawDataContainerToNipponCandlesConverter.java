package com.xzymon.sylar.processing;

import com.xzymon.sylar.constants.ChartType;
import com.xzymon.sylar.constants.DayBy15MinuteIntervalsForBarChart;
import com.xzymon.sylar.constants.StockTradingDaysGenerator;
import com.xzymon.sylar.helper.DatesHelper;
import com.xzymon.sylar.helper.Helper;
import com.xzymon.sylar.model.FrameCoords;
import com.xzymon.sylar.model.NipponCandle;
import com.xzymon.sylar.model.RawDataContainer;
import com.xzymon.sylar.model.RawDataNipponCandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RawDataContainerToNipponCandlesConverter {
	private static final Logger LOGGER = LoggerFactory.getLogger(RawDataContainerToNipponCandlesConverter.class);

	public static Map<Integer, NipponCandle> convert(RawDataContainer rawDataContainer) {
		Map<Integer, NipponCandle> result = new LinkedHashMap<>();
		int count = 0;
		FrameCoords extremalPoints = rawDataContainer.getValueSeriesExtremalPoints();
		int leftMostX = extremalPoints.getLeft();
		int rightMostX = extremalPoints.getRight();
		Integer alternativePoint1 = rawDataContainer.getTextToVG().get(ChartType.BAR.getFirstAlternativeTimePointText());
		Integer alternativePoint2 = rawDataContainer.getTextToVG().get(ChartType.BAR.getLastAlternativeTimePointText());
		int dividerI = (alternativePoint2 - alternativePoint1) / ChartType.BAR.getBetweenTimePointsCount();
		int halfDIviderI = dividerI / 2;
		double betweenTimePointsD = ChartType.BAR.getBetweenTimePointsCount();
		double dividerD = (alternativePoint2 - alternativePoint1) / betweenTimePointsD;

		//int dividerI = (rightMostX - leftMostX ) / ChartType.BAR.getExpectedValuePointsCount();
		//int halfDIviderI = dividerI / 2;
		//double barCountD = ChartType.BAR.getExpectedValuePointsCount();
		//double dividerD = (rightMostX - leftMostX) / barCountD;
		Integer position = null;
		NipponCandle ncInter;
		Map<Integer, BigDecimal> hvMap = rawDataContainer.getHorizontalValuesMap();
		String dateString = rawDataContainer.getGeneratedDateTimeArea().getExtractedText();
		String reformatedDate = DatesHelper.getDateYYYYDashMMDashDD(dateString);
		int size = rawDataContainer.getCandles().size();

		Map<Integer, Integer> referencePointToPositionMap = new HashMap<>();
		int startPoint = leftMostX + halfDIviderI;
		int orientationPointI;
		double orientationPointD;
		int aroundStart, aroundEnd;

		int maxJ = 0;
		for (int i = 0; i < size; i++) {
			orientationPointD = startPoint + (i * dividerD);
			orientationPointI = (int) Math.round(orientationPointD);
			aroundStart = orientationPointI - halfDIviderI;
			aroundEnd = orientationPointI + halfDIviderI;
			int j = aroundStart;
			while (j <= aroundEnd) {
				referencePointToPositionMap.put(j, i);
				j++;
			}
			maxJ = aroundEnd;
		}
		for (int i = 0; i < maxJ; i++) {
			if (i % 10 == 0) {
				System.out.println(String.format("--- %1$d ---", i));
			}
			System.out.println(String.format("    %1$d -> %2$d", i, referencePointToPositionMap.get(i)));
		}

		for (RawDataNipponCandle candle : rawDataContainer.getCandles()) {
			System.out.println(String.format("Processing Candle[%1$d]: %2$s", count, candle));
			ncInter = new NipponCandle();
			ncInter.setDateString(reformatedDate);
			position = referencePointToPositionMap.get(candle.getDatetimeMarker());
			if (position == null) {
				throw new RuntimeException(String.format("Position is null! For: candle[%1$d], datetimeMarker: %2$d", count, candle.getDatetimeMarker()));
			}
			ncInter.setTimeString(DayBy15MinuteIntervalsForBarChart.TIME_POINTS.get(position));
			ncInter.setOpen(hvMap.get(candle.getOpen()));
			ncInter.setHigh(hvMap.get(candle.getHigh()));
			ncInter.setLow(hvMap.get(candle.getLow()));
			ncInter.setClose(hvMap.get(candle.getClose()));
			System.out.println(String.format("Putting Interpretation[%1$d]", count, candle));
			result.put(position, ncInter);
			count++;
		}
		System.out.println(String.format("Processed Candles count: %1$s", count));
		if (count != rawDataContainer.getCandles().size()) {
			LOGGER.error(String.format("Candles count mismatch: %1$d != %2$d", count, rawDataContainer.getCandles().size()));
		}
		return result;
	}

	public static NipponCandle convertPreviousDayClose(RawDataContainer rawDataContainer) {
		int valueToMap = rawDataContainer.getPreviousDayClose();
		NipponCandle result = new NipponCandle();
		String currentDateString = rawDataContainer.getGeneratedDateTimeArea().getExtractedText();
		String reformatedCurrentDateString = DatesHelper.getDateYYYYDashMMDashDD(currentDateString);
		StockTradingDaysGenerator stockTradingDaysGenerator = new StockTradingDaysGenerator();
		List<String> stockDays = stockTradingDaysGenerator.generate();
		int currentDayIndex = stockDays.indexOf(reformatedCurrentDateString);
		int previousDayIndex = currentDayIndex - 1;
		String previousDateString = stockDays.get(previousDayIndex);
		result.setDateString(previousDateString);
		result.setTimeString(DayBy15MinuteIntervalsForBarChart.PREVIOUS_DAY_LAST_TIME_POINT);
		BigDecimal sharedValue = rawDataContainer.getHorizontalValuesMap().get(valueToMap);
		result.setOpen(sharedValue);
		result.setHigh(sharedValue);
		result.setLow(sharedValue);
		result.setClose(sharedValue);
		return result;
	}
}
