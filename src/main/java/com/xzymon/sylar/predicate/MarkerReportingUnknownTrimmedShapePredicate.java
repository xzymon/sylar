package com.xzymon.sylar.predicate;

import com.xzymon.sylar.constants.marker.MarkerCharacter;

import java.util.Map;

public class MarkerReportingUnknownTrimmedShapePredicate extends ReportingUnknownTrimmedShapePredicate {
    public static final double TOLERANCE_THRESHOLD_PERCENT = 0.95;
    public static final Map<Integer, Map<int[], String>> WIDTHS_MAP  = MarkerCharacter.WIDTHS_MAP;

    double getToleranceThresholdPercent() {
        return TOLERANCE_THRESHOLD_PERCENT;
    }

    Map<Integer, Map<int[], String>> getWidthsMap() {
        return WIDTHS_MAP;
    }
}
