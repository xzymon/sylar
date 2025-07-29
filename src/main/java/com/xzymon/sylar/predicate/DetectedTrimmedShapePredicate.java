package com.xzymon.sylar.predicate;

import com.xzymon.sylar.model.PixelShapeContainer;
import org.slf4j.Logger;

import java.util.function.Predicate;

@FunctionalInterface
public interface DetectedTrimmedShapePredicate extends Predicate<PixelShapeContainer> {

    default void checkAndLogMessage(PixelShapeContainer pixelShapeContainer, Logger log) {
        boolean raise = false;
        int offset = pixelShapeContainer.getOffset();
        int areaWidth = pixelShapeContainer.getAreaWidth();
        int[] pixelArray = pixelShapeContainer.getPixelArray();
        int arrWidth = pixelShapeContainer.getArrWidth();
        int arrHeight = pixelShapeContainer.getArrHeight();
        String message = pixelShapeContainer.getMessage();
        //check array params correctness
        if (pixelArray == null) {
            log.info("pixelArray is null");
            raise = true;
        }
        if (arrWidth <= 0) {
            log.info("arrWidth is no more than 0");
            raise = true;
        }
        if (arrHeight <= 0) {
            log.info("arrHeight is no more than 0");
            raise = true;
        }
        raiseException(raise);
        if (pixelArray.length != arrWidth * arrHeight) {
            log.info("pixelArray is {}", pixelArray.length);
            log.info("pixelArray length != arrWidth * arrHeight");
            raise = true;
        }
        if (offset >= arrWidth) {
            log.info("offset >= arrWidth");
            raise = true;
        }
        if (areaWidth > arrWidth) {
            log.info("areaWidth > arrWidth");
            raise = true;
        }
        raiseException(raise);
        log.info(message);
    }

    default void raiseException(boolean flag) {
        if (flag) throw new RuntimeException("Detected trimmed chars predicate failed. See logs above.");
    }

    /**
     * Zakładając, że litera lub cyfra lub znak specjalny (lub ich zlepek) to pojedynczy symbol
     * - ta metoda wycina ten symbol z całego obszaru dostarczonego w pixelArray
     *
     * @param areaWidth the width of the area to extract from the pixel array
     * @param arrHeight the height of the area to extract from the pixel array
     * @param offset the horizontal offset applied to each subsequent row while extracting data
     * @param pixelArray the original pixel array representing the complete image
     * @param arrWidth the width of the original pixel array
     * @return an array of integers containing the extracted pixel data
     */
    default int[] getForegroundShape(int areaWidth, int arrHeight, int offset, int[] pixelArray, int arrWidth) {
        int[] subArea = new int[areaWidth * arrHeight];
        int currentPixelIndex = 0;
        int subareaPixelIndex = 0;
        int lineStartIndex;
        while (subareaPixelIndex < areaWidth * arrHeight) {
            lineStartIndex = currentPixelIndex;
            currentPixelIndex += offset;
            for (int i = 0; i < areaWidth; i++) {
                //log.info("currentPixelIndex={}, lineStartIndex={}, i={}", currentPixelIndex, lineStartIndex, i);
                subArea[subareaPixelIndex+i] = pixelArray[currentPixelIndex+i];
            }
            subareaPixelIndex += areaWidth;
            currentPixelIndex = lineStartIndex + arrWidth;
        }
        return subArea;
    }
}
