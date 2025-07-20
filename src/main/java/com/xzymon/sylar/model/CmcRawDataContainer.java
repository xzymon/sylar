package com.xzymon.sylar.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
}
