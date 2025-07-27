package com.xzymon.sylar.constants;

import java.util.HashMap;
import java.util.Map;

public class MonthPlMapping {
	public static final Map<String, String> MMM_TO_MM = new HashMap<>();
	public static final Map<String, Integer> MAC_MMM_TO_INT = new HashMap<>();

	static {
		MMM_TO_MM.put("Sty", "01");
		MMM_TO_MM.put("Lut", "02");
		MMM_TO_MM.put("Mar", "03");
		MMM_TO_MM.put("Kwi", "04");
		MMM_TO_MM.put("Maj", "05");
		MMM_TO_MM.put("Cze", "06");
		MMM_TO_MM.put("Lip", "07");
		MMM_TO_MM.put("Sie", "08");
		MMM_TO_MM.put("Wrz", "09");
		MMM_TO_MM.put("Paz", "10");
		MMM_TO_MM.put("Lis", "11");
		MMM_TO_MM.put("Gru", "12");

		MAC_MMM_TO_INT.put("sty", 1);
		MAC_MMM_TO_INT.put("lut", 2);
		MAC_MMM_TO_INT.put("mar", 3);
		MAC_MMM_TO_INT.put("kwi", 4);
		MAC_MMM_TO_INT.put("maj", 5);
		MAC_MMM_TO_INT.put("cze", 6);
		MAC_MMM_TO_INT.put("lip", 7);
		MAC_MMM_TO_INT.put("sie", 8);
		MAC_MMM_TO_INT.put("wrz", 9);
		MAC_MMM_TO_INT.put("paz", 10);
		MAC_MMM_TO_INT.put("lis", 11);
		MAC_MMM_TO_INT.put("gru", 12);
	}
}
