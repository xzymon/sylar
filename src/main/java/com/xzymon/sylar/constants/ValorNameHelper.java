package com.xzymon.sylar.constants;

import java.util.HashMap;
import java.util.Map;

public class ValorNameHelper {
    public static final Map<String, String> VALOR_NAME_MAP = new HashMap<String, String>();

    static {
        VALOR_NAME_MAP.put("USD/JPY", "USDJPY");
        VALOR_NAME_MAP.put("USD/PLN", "USDPLN");
    }
}
