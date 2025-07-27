package com.xzymon.sylar.predicate;

import com.xzymon.sylar.model.PixelShapeContainer;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public abstract class ReportingUnknownTrimmedShapePredicate extends PrintTrimmedMonoShapePredicate {

    @Override
    public boolean test(PixelShapeContainer pixelShapeContainer) {
        refineArea(pixelShapeContainer);
        return true;
    }

    private void refineArea(PixelShapeContainer pixelShapeContainer) {
        int offset = pixelShapeContainer.getOffset() ;
        int areaWidth = pixelShapeContainer.getAreaWidth();
        int[] pixelArray = pixelShapeContainer.getPixelArray();
        int arrWidth = pixelShapeContainer.getArrWidth();
        int arrHeight = pixelShapeContainer.getArrHeight();
        String message = pixelShapeContainer.getMessage();

        checkAndLogMessage(pixelShapeContainer, log);
        int[] foregroundShape = getForegroundShape(areaWidth, arrHeight, offset, pixelArray, arrWidth);
        String foundShape = findShapeInFontOrRaise(foregroundShape, pixelShapeContainer);
        pixelShapeContainer.getExtractedText().append(foundShape);
    }

    private String findShapeInFontOrRaise(int[] foregroundShape, PixelShapeContainer pixelShapeContainer) {
        Map<int[], String> widthMap = getWidthsMap().get(pixelShapeContainer.getAreaWidth());
        if (widthMap == null) {
            throw new RuntimeException("Atypical trimmed shape found. There is no map for such width: " + pixelShapeContainer.getAreaWidth());
        }
        boolean matchWithinTolerance = false;
        String matchWithinToleranceChar = null;
        for (Map.Entry<int[], String> consideredChar : widthMap.entrySet() ) {
            if (consideredChar.getKey().length == foregroundShape.length) {
                int compliantEntirely = foregroundShape.length;
                int compliantWithinTolerance = (int) (foregroundShape.length * getToleranceThresholdPercent());
                int maxToleranceDifference = compliantEntirely - compliantWithinTolerance;
                int toleranceDifference = 0;
                for (int i = 0; i < consideredChar.getKey().length; i++) {
                    if (consideredChar.getKey()[i] != translateToMonoBinaryPixel(foregroundShape[i], foregroundShape[0])) {
                        toleranceDifference++;
                    }
                    if (toleranceDifference > maxToleranceDifference) {
                        break;
                    }
                }
                if (toleranceDifference == 0) {
                    log.debug("Exact match - Found char {} with width {}", consideredChar.getValue(), pixelShapeContainer.getAreaWidth());
                    return consideredChar.getValue();
                }
                if (toleranceDifference <= maxToleranceDifference) {
                    log.debug("Match within tolerance - Found char {} with width {}. toleranceDifference / compliantEntirely = {} / {}", consideredChar.getValue(), pixelShapeContainer.getAreaWidth(), toleranceDifference, foregroundShape.length);
                    log.debug("Matched char: {}", consideredChar.getValue());
                    log.debug("Will continue to find even better match...");
                    matchWithinTolerance = true;
                    matchWithinToleranceChar = consideredChar.getValue();
                }
            } else {
                throw new RuntimeException("Atypical trimmed shape found. Shape length is not equal to considered char font width. Problem with shape - or with considered char? Considered char: " + consideredChar.getValue());
            }
        }
        if (matchWithinTolerance) {
            log.debug("No exact match. Found match within tolerance - Found char {} with width {}", matchWithinToleranceChar, pixelShapeContainer.getAreaWidth());
            return matchWithinToleranceChar;
        }
        log.error("No match found for shape {} with width {}", foregroundShape, pixelShapeContainer.getAreaWidth());
        printShapeHumanReadable(pixelShapeContainer.getAreaWidth(), pixelShapeContainer.getArrHeight(), foregroundShape);
        printShapeFlatenedMonoBinary(pixelShapeContainer.getAreaWidth(), pixelShapeContainer.getArrHeight(), foregroundShape);
        throw new RuntimeException("No match found for shape. See the logs above. Can't continue.");
    }

    int translateToMonoBinaryPixel(int pixel, int backgroundColor) {
        return pixel == backgroundColor ? 0 : 1;
    }

    abstract double getToleranceThresholdPercent();

    abstract Map<Integer, Map<int[], String>> getWidthsMap();
}
