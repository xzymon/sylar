package com.xzymon.sylar.predicate;

import com.xzymon.sylar.constants.fontgauges.FontGaugesFloatingCharacters;

import java.util.Map;

public class FontGaugesReportingUnknownTrimmedShapePredicate extends ReportingUnknownTrimmedShapePredicate {
	public static final double TOLERANCE_THRESHOLD_PERCENT = 0.95;
	public static final Map<Integer, Map<int[], String>> WIDTHS_MAP  = FontGaugesFloatingCharacters.WIDTHS_MAP;

	double getToleranceThresholdPercent() {
		return TOLERANCE_THRESHOLD_PERCENT;
	}

	Map<Integer, Map<int[], String>> getWidthsMap() {
		return WIDTHS_MAP;
	}
}
