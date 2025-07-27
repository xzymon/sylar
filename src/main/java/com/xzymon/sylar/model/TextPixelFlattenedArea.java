package com.xzymon.sylar.model;

import lombok.Data;

@Data
public class TextPixelFlattenedArea {
    private int[] pixelArea;
    private int xLength;
    private int yLength;
    private String extractedText;
}
