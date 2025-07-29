package com.xzymon.sylar.constants;

import java.util.HashMap;
import java.util.Map;

public class IntervalHelper {
    public static final Map<String, String> UNITS_MAP = new HashMap<>();
    public static final Map<String, String> LENGTHS_MAP = new HashMap<>();
    public static final Map<String, String> LENGTH_WITH_UNIT_MAP = new HashMap<>();

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

        LENGTH_WITH_UNIT_MAP.put("1m", "1m");
        LENGTH_WITH_UNIT_MAP.put("5m", "5m");
        LENGTH_WITH_UNIT_MAP.put("10m", "10m");
        LENGTH_WITH_UNIT_MAP.put("15m", "15m");
        LENGTH_WITH_UNIT_MAP.put("30m", "30m");
        LENGTH_WITH_UNIT_MAP.put("1h", "1h");
        LENGTH_WITH_UNIT_MAP.put("5s", "5s");
        LENGTH_WITH_UNIT_MAP.put("10s", "10s");
        LENGTH_WITH_UNIT_MAP.put("30s", "30s");
    }
}
