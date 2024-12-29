package com.xzymon.sylar.model;

import com.xzymon.sylar.constants.ChartType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RawDataContainer {
	private FrameCoords valuesFrame;                     //granice wykresu notowań
	private FrameCoords valueSeriesExtremalPoints;       //typ nie do końca pasuje, sama obecność tego jest dla celów optymalizacji
	private FrameCoords volumeFrame;                     //granice wykresu obrotów
	private FrameCoords volumeSeriesExtremalPoints;      // jak 2 pozycje wyżej
	private List<RawValueInBuckets> valuesPerHorizontal; //notowania (liczone kubełkmi)
	private List<RawValueInBuckets> volumePerHorizontal; //obroty (liczone kubełkami)
	private List<VerticalLineBucket> valueVLBuckets;     //kubełki (odpowiadające liniom) dla notowań
	private Integer previousDayClose = null;             //zamknięcie poprzedniego dnia - czerwona linia na wykresie
	private NipponCandle entireChartCandle;              //pojedyncza świeca reprezentująca cały wykres

	private TextPixelArea valorNameArea;                 //nazwa waloru - obszar obrazu do ekstrakcji tekstu
	private TextPixelArea generatedDateTimeArea;         //data i czas publikacji danych wykresu w tej postaci - obszar obrazu do ekstrakcji tekstu
	private Map<Integer, TextPixelArea> horizontalGauges;//mapa dla prowadnic poziomych - obszar obrazu do ekstrakcji tekstu
	private Map<Integer, TextPixelArea> verticalGauges;  //mapa dla prowadnic pionowych - obszar obrazu do ekstrakcji tekstu
	private TextPixelArea intervalArea;                  //interval - obszar obrazu do ekstrakcji tekstu
	private ChartType chartType;

	private Map<Integer, BigDecimal> horizontalValuesMap;//mapowanie położenia na wykresie na wartość
	private List<NipponCandle> candles;

	public RawDataContainer() {
		this.valuesFrame = new FrameCoords();
		this.valueSeriesExtremalPoints = new FrameCoords();
		this.volumeFrame = new FrameCoords();
		this.volumeSeriesExtremalPoints = new FrameCoords();
		this.valuesPerHorizontal = new ArrayList<>();
		this.volumePerHorizontal = new ArrayList<>();
		this.valueVLBuckets = new ArrayList<>();
		this.horizontalGauges = new HashMap<>();
		this.verticalGauges = new HashMap<>();
		this.horizontalValuesMap = new HashMap<>();
		this.candles = new ArrayList<>();
	}

	public FrameCoords getValuesFrame() {
		return valuesFrame;
	}

	public void setValuesFrame(FrameCoords valuesFrame) {
		this.valuesFrame = valuesFrame;
	}

	public FrameCoords getValueSeriesExtremalPoints() {
		return valueSeriesExtremalPoints;
	}

	public void setValueSeriesExtremalPoints(FrameCoords valueSeriesExtremalPoints) {
		this.valueSeriesExtremalPoints = valueSeriesExtremalPoints;
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

	public FrameCoords getVolumeSeriesExtremalPoints() {
		return volumeSeriesExtremalPoints;
	}

	public void setVolumeSeriesExtremalPoints(FrameCoords volumeSeriesExtremalPoints) {
		this.volumeSeriesExtremalPoints = volumeSeriesExtremalPoints;
	}

	public List<VerticalLineBucket> getValueVLBuckets() {
		return valueVLBuckets;
	}

	public void setValueVLBuckets(List<VerticalLineBucket> valueVLBuckets) {
		this.valueVLBuckets = valueVLBuckets;
	}

	public NipponCandle getEntireChartCandle() {
		return entireChartCandle;
	}

	public void setEntireChartCandle(NipponCandle entireChartCandle) {
		this.entireChartCandle = entireChartCandle;
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

	public Map<Integer, TextPixelArea> getHorizontalGauges() {
		return horizontalGauges;
	}

	public void setHorizontalGauges(Map<Integer, TextPixelArea> horizontalGauges) {
		this.horizontalGauges = horizontalGauges;
	}

	public Map<Integer, TextPixelArea> getVerticalGauges() {
		return verticalGauges;
	}

	public void setVerticalGauges(Map<Integer, TextPixelArea> verticalGauges) {
		this.verticalGauges = verticalGauges;
	}

	public TextPixelArea getIntervalArea() {
		return intervalArea;
	}

	public void setIntervalArea(TextPixelArea intervalArea) {
		this.intervalArea = intervalArea;
	}

	public ChartType getChartType() {
		return chartType;
	}

	public void setChartType(ChartType chartType) {
		this.chartType = chartType;
	}

	public Map<Integer, BigDecimal> getHorizontalValuesMap() {
		return horizontalValuesMap;
	}

	public void setHorizontalValuesMap(Map<Integer, BigDecimal> horizontalValuesMap) {
		this.horizontalValuesMap = horizontalValuesMap;
	}

	public int getPreviousDayClose() {
		return previousDayClose;
	}

	public void setPreviousDayClose(int previousDayClose) {
		this.previousDayClose = previousDayClose;
	}

	public List<NipponCandle> getCandles() {
		return candles;
	}

	public void setCandles(List<NipponCandle> candles) {
		this.candles = candles;
	}
}
