package com.xzymon.sylar.predicate;

import com.xzymon.sylar.helper.ColorReplacementHelper;

/**
 * Tak, nazwa tej klasy to jest potworek, ale trudno było znaleźć dobrą nazwę.
 * To nadbudówka nad klasą MarkerReportingUnknownTrimmedShapePredicate.
 * MarkerReportingUnknownTrimmedShapePredicate służy do rozpoznawania specjalnych kształtów - takich które nie są alfanumeryczne.
 * Natomiast ta klasa to jej rozszerzona wersja - która ma dodatkowo wprowadzone progowanie kolorów w celu ujednoznacznienia znaków.
 *
 * Podmianę kolorów realizuje metoda performAdvancedRecoloring.
 * Metoda ta zagląda w kanały kolorów i sprawdza czy wartość dla tego kanału jest powyżej ustalonego progu.
 * Jeśli tak, to zwraca oryginalny kolor piksela, w przeciwnym wypadku zwraca ustaloną wartość podmiany.
 * Ustalona wartość podmiany to czarny kolor (BELOW_MARGIN_REPLACEMENT).
 */
public class AdvancedRecoloring60MarkerReportingUnknownTrimmedShapePredicate extends MarkerReportingUnknownTrimmedShapePredicate {
	public static final int CHANNEL_RED_MARGIN = 0x60;
	public static final int CHANNEL_GREEN_MARGIN = 0x60;
	public static final int CHANNEL_BLUE_MARGIN = 0x60;
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
}
