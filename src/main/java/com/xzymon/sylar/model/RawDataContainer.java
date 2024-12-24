package com.xzymon.sylar.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RawDataContainer {
	private FrameCoords valuesFrame;                    //granice wykresu notowań
	private FrameCoords volumeFrame;                    //granice wykresu obrotów
	private List<RawValueInBuckets> valuesPerHorizontal;//notowania (liczone kubełkmi)
	private List<RawValueInBuckets> volumePerHorizontal;//obroty (liczone kubełkami)
	private List<VerticalLineBucket> valueVLBuckets;    //kubełki (odpowiadające liniom) dla notowań
	private Integer dayOpeningBucket;                   //linia kubełka wartości otwarcia dnia
	private Integer dayMaxBucket;                       //linia kubełka wartości max z dnia
	private Integer dayMinBucket;                       //linia kubełka wartości min z dnia
	private Integer dayClosingBucket;                   //linia kubełka wartości zamknięcia dnia

	private TextPixelArea valorNameArea;                //nazwa waloru - obszar obrazu do ekstrakcji tekstu
	private TextPixelArea generatedDateTimeArea;        //data i czas publikacji danych wykresu w tej postaci - obszar obrazu do ekstrakcji tekstu
	private Map<Integer, TextPixelArea> horizontalGauges;  //mapa dla prowadnic poziomych - obszar obrazu do ekstrakcji tekstu
	private Map<Integer, TextPixelArea> verticalGauges;    //mapa dla prowadnic pionowych - obszar obrazu do ekstrakcji tekstu
	private TextPixelArea intervalArea;                 //interval - obszar obrazu do ekstrakcji tekstu

	public RawDataContainer() {
		this.valuesFrame = new FrameCoords();
		this.volumeFrame = new FrameCoords();
		this.valuesPerHorizontal = new ArrayList<>();
		this.volumePerHorizontal = new ArrayList<>();
		this.valueVLBuckets = new ArrayList<>();
		this.horizontalGauges = new HashMap<>();
		this.verticalGauges = new HashMap<>();
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
}