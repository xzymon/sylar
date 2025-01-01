package com.xzymon.sylar.helper;

import com.xzymon.sylar.constants.MonthPlMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatesHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(DatesHelper.class);

	public static String getDateYYYYDashMMDashDD(String dateString) {
		//expected format: 30 Wrz 2024 23:59 CEST
		String trimmed = dateString.trim();
		String[] dateParts = trimmed.split(" ");
		if (dateParts.length != 5) {
			LOGGER.error(String.format("Unexpected date format: %1$s", dateString));
			throw new RuntimeException("Unexpected date format: " + dateString);
		}
		String dd = dateParts[0];
		if (dd.length() == 1) {
			dd = "0" + dd;
		}
		String mmm = dateParts[1];
		String yyyy = dateParts[2];
		String result = yyyy + "-" + MonthPlMapping.MMM_TO_MM.get(mmm) + "-" + dd;
		return result;
	}

	public static String getDateYYYYMMDD(String dateString) {
		//expected format: 30 Wrz 2024 23:59 CEST
		String trimmed = dateString.trim();
		String[] dateParts = trimmed.split(" ");
		if (dateParts.length != 5) {
			LOGGER.error(String.format("Unexpected date format: %1$s", dateString));
			throw new RuntimeException("Unexpected date format: " + dateString);
		}
		String dd = dateParts[0];
		if (dd.length() == 1) {
			dd = "0" + dd;
		}
		String mmm = dateParts[1];
		String yyyy = dateParts[2];
		String result = yyyy + MonthPlMapping.MMM_TO_MM.get(mmm) + dd;
		return result;
	}
}
