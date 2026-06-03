package com.xzymon.sylar.helper;

import java.util.Map;

public class ColorReplacementHelper {
	public static final int BLACK_000000 = -16777216; // 0xff000000 = #000000

	public static final int GRAY_363636 = -13224394; // 0xff363636 = #363636
	public static final int GRAY_5B5B5B = -10790053; // 0xff5b5b5b = #5b5b5b
	public static final int GRAY_CACACA = -3487030; // 0xffcacaca = #cacaca

	public static final int BARELY_BLACK_121212 = -15592942; // 0xff121212 = #121212
	public static final int BARELY_BLACK_PLUS_B_121213 = -15592942; // 0xff121213 = #121213
	public static final int BARELY_BLACK_PLUS_G_121312 = -15592686; // 0xff121312 = #121312
	public static final int BARELY_BLACK_PLUS_GB_121313 = -15592685; // 0xff121313 = #121313
	public static final int BARELY_BLACK_PLUS_R_131212 = -15527406; // 0xff131212 = #131212
	public static final int BARELY_BLACK_PLUS_RB_131213 = -15527405; // 0xff131213 = #131213
	public static final int BARELY_BLACK_PLUS_RG_131312 = -15527150; // 0xff131312 = #131312
	public static final int BARELY_BLACK_PLUS_RGB_131313 = -15527149; // 0xff131313 = #131313

	public static final Map<Integer, Integer> NO_REPLACEMENTS = Map.of();

	public static final Map<Integer, Integer> CURRENT_LEVEL_LINE_ERASER = Map.of(
			GRAY_CACACA, BLACK_000000	// #cacaca -> #000
	);

	public static final Map<Integer, Integer> INTERVAL_OPTIONS = Map.of(
			GRAY_363636, BLACK_000000,   // #363636 -> #000
			GRAY_5B5B5B, BLACK_000000,   // #5b5b5b -> #000
			GRAY_CACACA, BLACK_000000    // #cacaca -> #000
	);

	public static final Map<Integer, Integer> BARELY_BLACK_DEVIATIONS = Map.of(
			BARELY_BLACK_PLUS_B_121213, BARELY_BLACK_121212, // #121213 -> #121212
			BARELY_BLACK_PLUS_G_121312, BARELY_BLACK_121212, // #121312 -> #121212
			BARELY_BLACK_PLUS_GB_121313, BARELY_BLACK_121212, // #121313 -> #121212
			BARELY_BLACK_PLUS_R_131212, BARELY_BLACK_121212, // #131212 -> #121212
			BARELY_BLACK_PLUS_RB_131213, BARELY_BLACK_121212, // #131213 -> #121212
			BARELY_BLACK_PLUS_RG_131312, BARELY_BLACK_121212, // #131312 -> #121212
			BARELY_BLACK_PLUS_RGB_131313, BARELY_BLACK_121212 // #131313 -> #121212
	);
}
