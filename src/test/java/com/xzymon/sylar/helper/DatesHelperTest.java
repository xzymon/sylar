package com.xzymon.sylar.helper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatesHelperTest {

	@Test
	void getDateYYYYDashMMDashDD_20240930() {
		String dateString = "30 Wrz 2024 23:59 CEST";
		String result = DatesHelper.getDateYYYYDashMMDashDD(dateString);
		assertEquals("2024-09-30", result);
	}

	@Test
	void getDateYYYYDashMMDashDD_20241203() {
		String dateString = "3 Gru 2024 23:59 CEST";
		String result = DatesHelper.getDateYYYYDashMMDashDD(dateString);
		assertEquals("2024-12-03", result);
	}

	@Test
	void getDateYYYYMMDD_20240930() {
		String dateString = "30 Wrz 2024 23:59 CEST";
		String result = DatesHelper.getDateYYYYMMDD(dateString);
		assertEquals("20240930", result);
	}
	@Test
	void getDateYYYYMMDD_20241203() {
		String dateString = "3 Gru 2024 23:59 CEST";
		String result = DatesHelper.getDateYYYYMMDD(dateString);
		assertEquals("20241203", result);
	}
}