package com.xzymon.sylar.predicate;

import com.xzymon.sylar.constants.recoloring30.fontsize17.Recoloring30FontSize17FloatingCharacters;
import com.xzymon.sylar.helper.ColorReplacementHelper;

import java.util.Map;

/**
 * Advanced recoloring predicate for font size 17 with reporting unknown trimmed shapes.
 * Uses a 30 margin for recoloring and replaces pixels below the margin with black.
 *
 * Ta klasa jest stosowana do wykrywania tekstu w obszarze time axis.
 * Przyczyna jej wyodrębnienia od FontSize17ReportingUnknownTrimmedShapePredicate jest taka - że na tekst na time axis
 * wciskają się prowadnice pionowe - i uznałem, że najlepiej jest je wycinać poprzez progowanie kolorów.
 *
 * @see AdvancedRecoloring60MarkerReportingUnknownTrimmedShapePredicate
 */
public class AdvancedRecoloring30FontSize17ReportingUnknownTrimmedShapePredicate extends ReportingUnknownTrimmedShapePredicate {
	public static final double TOLERANCE_THRESHOLD_PERCENT = 0.95;
	public static final Map<Integer, Map<int[], String>> WIDTHS_MAP  = Recoloring30FontSize17FloatingCharacters.WIDTHS_MAP;

	public static final int CHANNEL_RED_MARGIN = 0x30;
	public static final int CHANNEL_GREEN_MARGIN = 0x30;
	public static final int CHANNEL_BLUE_MARGIN = 0x30;
	public static final int BELOW_MARGIN_REPLACEMENT = ColorReplacementHelper.BLACK_000000;

	@Override
	protected int performAdvancedRecoloring(int pixel) {
		int red = (pixel >> 16) & 0xFF;
		int green = (pixel >> 8) & 0xFF;
		int blue = pixel & 0xFF;
		if (red > CHANNEL_RED_MARGIN || green > CHANNEL_GREEN_MARGIN || blue > CHANNEL_BLUE_MARGIN) {
			return pixel;
		}
		return BELOW_MARGIN_REPLACEMENT;
	}

	@Override
	protected boolean hasAdvancedRecoloring() {
		return true;
	}

	double getToleranceThresholdPercent() {
		return TOLERANCE_THRESHOLD_PERCENT;
	}

	Map<Integer, Map<int[], String>> getWidthsMap() {
		return WIDTHS_MAP;
	}
}
