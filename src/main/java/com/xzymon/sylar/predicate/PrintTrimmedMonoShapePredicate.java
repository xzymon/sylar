package com.xzymon.sylar.predicate;

import com.xzymon.sylar.model.PixelShapeContainer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrintTrimmedMonoShapePredicate implements DetectedTrimmedShapePredicate {
    @Override
    public boolean test(PixelShapeContainer pixelShapeContainer) {
        printArea(pixelShapeContainer);
        return true;
    }

    private void printArea(PixelShapeContainer pixelShapeContainer) {
        int offset = pixelShapeContainer.getOffset() ;
        int areaWidth = pixelShapeContainer.getAreaWidth();
        int[] pixelArray = pixelShapeContainer.getPixelArray();
        int arrWidth = pixelShapeContainer.getArrWidth();
        int arrHeight = pixelShapeContainer.getArrHeight();
        String message = pixelShapeContainer.getMessage();

        checkAndLogMessage(pixelShapeContainer, log);
        int[] foregroundShape = getForegroundShape(areaWidth, arrHeight, offset, pixelArray, arrWidth);
        printShapeHumanReadable(areaWidth, arrHeight, foregroundShape);
        printShapeFlatenedMonoBinary(areaWidth, arrHeight, foregroundShape);
    }

    static void printShapeHumanReadable(int areaWidth, int arrHeight, int[] subArea) {
        for (int i = 0; i < arrHeight; i++) {
            for (int j = 0; j < areaWidth; j++) {
                printMonoPixel(subArea[i* areaWidth + j], subArea[0]);
            }
            System.out.println();
        }
    }

    static void printShapeFlatenedMonoBinary(int areaWidth, int arrHeight, int[] subArea) {
        System.out.println("width: " + areaWidth);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arrHeight; i++) {
            for (int j = 0; j < areaWidth; j++) {
                sb.append(getMonoBinaryChar(subArea[i* areaWidth + j], subArea[0])).append(",");
            }
        }
        System.out.println("public static final int[] CHAR_1 = {" + sb + "};");
    }

    static void printMonoPixel(int pixel, int backgroundColor) {
        char color = pixel == backgroundColor ? ' ' : '#';
        System.out.print(color);
    }

    static char getMonoBinaryChar(int pixel, int backgroundColor) {
        return pixel == backgroundColor ? '0' : '1';
    }
}
