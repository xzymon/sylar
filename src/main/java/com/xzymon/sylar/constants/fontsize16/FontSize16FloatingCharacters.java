package com.xzymon.sylar.constants.fontsize16;

import java.util.HashMap;
import java.util.Map;

public class FontSize16FloatingCharacters {

    //ze względu na problem z literką k piksele są pobierane tak naprawdę dla 17 wierszy, a nie 16 - ale to taki hack
    // bo wysokość liter tak naprawdę wynosi 16
    public static final int LETTER_HEIGHT = 16;

    public static final Map<int[], String> WIDTH_9_MAP = new HashMap<>();
    public static final Map<int[], String> WIDTH_4_MAP = new HashMap<>();
    public static final Map<int[], String> WIDTH_10_MAP = new HashMap<>();
    public static final Map<int[], String> WIDTH_16_MAP = new HashMap<>();

    public static final Map<Integer, Map<int[], String>> WIDTHS_MAP = new HashMap<>();

    static {
        WIDTHS_MAP.put(4, WIDTH_4_MAP);
        WIDTHS_MAP.put(9, WIDTH_9_MAP);
        WIDTHS_MAP.put(10, WIDTH_10_MAP);
        WIDTHS_MAP.put(16, WIDTH_16_MAP);

        WIDTH_4_MAP.put(FontSize16Width04.MONO_SPECIAL_CHARACTER_DOT_W4_A, ".");

        WIDTH_9_MAP.put(FontSize16Width09.MONO_LETTER_LOWER_S_W09_A, "s");

        WIDTH_10_MAP.put(FontSize16Width10.MONO_LETTER_LOWER_E_W10_A, "e");
        WIDTH_10_MAP.put(FontSize16Width10.MONO_LETTER_LOWER_K_W10_A, "k");

        WIDTH_16_MAP.put(FontSize16Width16.MONO_LETTER_LOWER_M_W16_A, "m");
    }
}
