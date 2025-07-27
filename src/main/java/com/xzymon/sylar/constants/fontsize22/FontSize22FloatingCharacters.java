package com.xzymon.sylar.constants.fontsize22;

import java.util.HashMap;
import java.util.Map;

public class FontSize22FloatingCharacters {

    public static final int LETTER_HEIGHT = 22;

    public static final Map<int[], String> WIDTH_10_MAP = new HashMap<>();
    public static final Map<int[], String> WIDTH_11_MAP = new HashMap<>();
    public static final Map<int[], String> WIDTH_12_MAP = new HashMap<>();
    public static final Map<int[], String> WIDTH_16_MAP = new HashMap<>();
    public static final Map<int[], String> WIDTH_33_MAP = new HashMap<>();
    public static final Map<int[], String> WIDTH_35_MAP = new HashMap<>();

    public static final Map<Integer, Map<int[], String>> WIDTHS_MAP = new HashMap<>();

    static {
        WIDTHS_MAP.put(10, WIDTH_10_MAP);
        WIDTHS_MAP.put(11, WIDTH_11_MAP);
        WIDTHS_MAP.put(12, WIDTH_12_MAP);
        WIDTHS_MAP.put(16, WIDTH_16_MAP);
        WIDTHS_MAP.put(33, WIDTH_33_MAP);
        WIDTHS_MAP.put(35, WIDTH_35_MAP);

        WIDTH_10_MAP.put(FontSize22Width10.MONO_DIGIT_1_W10_A, "1");

        WIDTH_11_MAP.put(FontSize22Width11.MONO_DIGIT_1_W11_A, "1");

        WIDTH_16_MAP.put(FontSize22Width16.MONO_DIGIT_0_W16_A, "0");
        WIDTH_16_MAP.put(FontSize22Width16.MONO_DIGIT_5_W16_A, "5");

        WIDTH_33_MAP.put(FontSize22Width33.MONO_GLUED_DIGIT_3_AND_0_W33_A, "30");
    }
}
