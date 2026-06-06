package com.xzymon.sylar.constants;

import java.util.HashMap;
import java.util.Map;

public class IntervalHelper {
    public static final Map<String, String> UNITS_MAP = new HashMap<>();
    public static final Map<String, String> LENGTHS_MAP = new HashMap<>();

    static {
        //UNITS_MAP.put("DAY", "DAY");
        //UNITS_MAP.put("HOUR", "HOUR");
        UNITS_MAP.put("m", "m");
        UNITS_MAP.put("sek.", "s");

        LENGTHS_MAP.put("1", "1");
        LENGTHS_MAP.put("5", "5");
        LENGTHS_MAP.put("10", "10");
        LENGTHS_MAP.put("15", "15");
        LENGTHS_MAP.put("30", "30");
    }
}
