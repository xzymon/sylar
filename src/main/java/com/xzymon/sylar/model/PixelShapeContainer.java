package com.xzymon.sylar.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PixelShapeContainer {
    private int offset;
    private int areaWidth;
    private int[] pixelArray;
    private int arrWidth;
    private int arrHeight;
    private String message;
}
