package com.xzymon.sylar.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class PixelShapeContainer {
    private int offset;
    private int areaWidth;
    private int[] pixelArray;
    private int arrWidth;
    private int arrHeight;
    private String message;
    private StringBuilder extractedText;
    private Map<Integer, Integer> replaceColors; //replace key color by value color
}
