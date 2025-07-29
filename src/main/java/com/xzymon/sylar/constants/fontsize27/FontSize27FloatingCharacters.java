package com.xzymon.sylar.constants.fontsize27;

import java.util.HashMap;
import java.util.Map;

public class FontSize27FloatingCharacters {

    public static final int LINE_HEIGHT = 36;
    public static final int LETTER_HEIGHT = 27;

    public static final Map<int[], String> WIDTH_15_MAP = new HashMap<>();
    public static final Map<int[], String> WIDTH_16_MAP = new HashMap<>();
    public static final Map<int[], String> WIDTH_18_MAP = new HashMap<>();
    public static final Map<int[], String> WIDTH_19_MAP = new HashMap<>();
    public static final Map<int[], String> WIDTH_21_MAP = new HashMap<>();
    public static final Map<int[], String> WIDTH_22_MAP = new HashMap<>();
    public static final Map<int[], String> WIDTH_32_MAP = new HashMap<>();
    public static final Map<int[], String> WIDTH_47_MAP = new HashMap<>();

    public static final Map<Integer, Map<int[], String>> WIDTHS_MAP = new HashMap<>();

    static {
        WIDTHS_MAP.put(15, WIDTH_15_MAP);
        WIDTHS_MAP.put(16, WIDTH_16_MAP);
        WIDTHS_MAP.put(18, WIDTH_18_MAP);
        WIDTHS_MAP.put(19, WIDTH_19_MAP);
        WIDTHS_MAP.put(21, WIDTH_21_MAP);
        WIDTHS_MAP.put(22, WIDTH_22_MAP);
        WIDTHS_MAP.put(32, WIDTH_32_MAP);
        WIDTHS_MAP.put(47, WIDTH_47_MAP);

        WIDTH_15_MAP.put(FontSize27Width15.MONO_LETTER_UPPER_J_W15_A, "J");

        WIDTH_16_MAP.put(FontSize27Width16.MONO_LETTER_UPPER_L_W16_A, "L");

        WIDTH_18_MAP.put(FontSize27Width18.MONO_LETTER_UPPER_P_W18_A, "P");

        WIDTH_19_MAP.put(FontSize27Width19.MONO_LETTER_UPPER_S_W19_A, "S");

        WIDTH_21_MAP.put(FontSize27Width21.MONO_LETTER_UPPER_N_W21_A, "N");
        WIDTH_21_MAP.put(FontSize27Width21.MONO_LETTER_UPPER_U_W21_A, "U");

        WIDTH_22_MAP.put(FontSize27Width22.MONO_LETTER_UPPER_Y_W22_A, "Y");

        WIDTH_32_MAP.put(FontSize27Width32.MONO_GLUED_LETTER_UPPER_D_FORWARD_SLASH_W32_A, "D/");

        WIDTH_47_MAP.put(FontSize27Width47.MONO_GLUED_LETTER_UPPER_D_FORWARD_SLASH_AND_J_W47_A, "D/J");
    }
}
