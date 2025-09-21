package com.xzymon.sylar.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.*;

@Data
public class CmcRawDataContainer {
    private String sourceFileName;

    private FrameCoords valuesFrame;                     //granice wykresu notowań
    private boolean valuesFrameFlag;
    private FrameCoords valueSeriesExtremalPoints;       //typ nie do końca pasuje, sama obecność tego jest dla celów optymalizacji
    private List<RawValueInBuckets> valuesPerHorizontal; //notowania (liczone kubełkmi)
    private List<VerticalLineBucket> valueVLBuckets;     //kubełki (odpowiadające liniom) dla notowań

    private FrameCoords snapshotDateTimeFC;
    private TextPixelFlattenedArea snapshotDateTimeArea;

    private FrameCoords notChartMarkerFC;                 //obszar po którym sprawdzam czy to jednak nie jest wykres
    private TextPixelFlattenedArea notChartMarkerArea;
    private boolean isNotChart;

    private FrameCoords toolboxMarkerFC;                 //obszar po którym sprawdzam czy wykres ma wyświetlone narzędzia rysowania
    private TextPixelFlattenedArea toolboxMarkerArea;
    private boolean toolboxFlag;

    private boolean narrowFlag;
    private boolean wideFlag;




    private String dateTimePartForNewFileName;
    private String pngFileNewName;

    private FrameCoords valorNameFC;
    private TextPixelFlattenedArea valorNameArea;        //nazwa waloru - obszar obrazu do ekstrakcji tekstu
    private FrameCoords intervalLine1FC;                 //interval - 1 linia - cyferki
    private TextPixelFlattenedArea intervalLine1Area;
    private FrameCoords intervalLine2FC;                 //interval - 2 linia - sek / m
    private TextPixelFlattenedArea intervalLine2Area;

    private FrameCoords intervalOptionsFC;               //interval - gdy wyświetlony jest tekst "Opcje"
    private TextPixelFlattenedArea intervalOptionsArea;

    private Map<Integer, TextPixelFlattenedArea> horizontalGauges;//mapa dla prowadnic poziomych - obszar obrazu do ekstrakcji tekstu
    private Map<Integer, TextPixelFlattenedArea> verticalGauges;  //mapa dla prowadnic pionowych - obszar obrazu do ekstrakcji tekstu

    private Map<Integer, BigDecimal> horizontalValuesMap;//mapowanie położenia na wykresie na wartość
    private List<RawDataNipponCandle> candles;

    public CmcRawDataContainer() {
        this.valuesFrame = new FrameCoords();
        this.valueSeriesExtremalPoints = new FrameCoords();
        this.valuesPerHorizontal = new ArrayList<>();
        this.valueVLBuckets = new ArrayList<>();
        this.snapshotDateTimeFC = new FrameCoords();
        this.snapshotDateTimeArea = new TextPixelFlattenedArea();
        this.notChartMarkerFC = new FrameCoords();
        this.notChartMarkerArea = new TextPixelFlattenedArea();
        this.isNotChart = false;

        this.toolboxMarkerFC = new FrameCoords();
        this.toolboxMarkerArea = new TextPixelFlattenedArea();
        this.toolboxFlag = false;

        this.narrowFlag = false;
        this.wideFlag = false;

        this.pngFileNewName = null;                     //bieda kod - ale niech będzie jawne że domyślnie null
        this.valorNameFC = new FrameCoords();
        this.valorNameArea = new TextPixelFlattenedArea();
        this.intervalLine1FC = new FrameCoords();
        this.intervalLine1Area = new TextPixelFlattenedArea();
        this.intervalLine2FC = new FrameCoords();
        this.intervalLine2Area = new TextPixelFlattenedArea();
        this.intervalOptionsFC = new FrameCoords();
        this.intervalOptionsArea = new TextPixelFlattenedArea();
        this.horizontalGauges = new HashMap<>();
        this.verticalGauges = new HashMap<>();
        this.horizontalValuesMap = new HashMap<>();
        this.candles = new ArrayList<>();
    }
}
