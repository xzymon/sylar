package com.xzymon.sylar.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TextPixelFlattenedArea {
    private int[] pixelArea;
    private int xLength;
    private int yLength;
    private String extractedText;
    private BigDecimal parsedBDValue;
}
