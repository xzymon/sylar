package com.xzymon.sylar.predicate;

import com.xzymon.sylar.constants.fontsize20.FontSize20FloatingCharacters;

import java.util.Map;

public class FontSize20ReportingUnknownTrimmedShapePredicate extends ReportingUnknownTrimmedShapePredicate {
    public static final double TOLERANCE_THRESHOLD_PERCENT = 0.95;
    public static final Map<Integer, Map<int[], String>> WIDTHS_MAP  = FontSize20FloatingCharacters.WIDTHS_MAP;

    double getToleranceThresholdPercent() {
        return TOLERANCE_THRESHOLD_PERCENT;
    }

    Map<Integer, Map<int[], String>> getWidthsMap() {
        return WIDTHS_MAP;
    }
}
