package com.xzymon.sylar.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class CmcRawDataContainer {
    private FrameCoords valuesFrame;                     //granice wykresu notowań
    private FrameCoords valueSeriesExtremalPoints;       //typ nie do końca pasuje, sama obecność tego jest dla celów optymalizacji
    private List<RawValueInBuckets> valuesPerHorizontal; //notowania (liczone kubełkmi)
    private List<VerticalLineBucket> valueVLBuckets;     //kubełki (odpowiadające liniom) dla notowań

    private TextPixelArea snapshotDateTimeArea;
    private TextPixelArea valorNameArea;                 //nazwa waloru - obszar obrazu do ekstrakcji tekstu
    private Map<Integer, TextPixelArea> horizontalGauges;//mapa dla prowadnic poziomych - obszar obrazu do ekstrakcji tekstu
    private Map<Integer, TextPixelArea> verticalGauges;  //mapa dla prowadnic pionowych - obszar obrazu do ekstrakcji tekstu
    private Map<String, Integer> textToVG;
    private TextPixelArea intervalArea;                  //interval - obszar obrazu do ekstrakcji tekstu

    private Map<Integer, BigDecimal> horizontalValuesMap;//mapowanie położenia na wykresie na wartość
    private List<RawDataNipponCandle> candles;

    public CmcRawDataContainer() {
        this.valuesFrame = new FrameCoords();
        this.valueSeriesExtremalPoints = new FrameCoords();
        this.valuesPerHorizontal = new ArrayList<>();
        this.valueVLBuckets = new ArrayList<>();
        this.horizontalGauges = new HashMap<>();
        this.textToVG = new HashMap<>();
        this.verticalGauges = new HashMap<>();
        this.horizontalValuesMap = new HashMap<>();
        this.candles = new ArrayList<>();
    }
}
