package com.xzymon.sylar.constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class StockTradingDaysGenerator {
	private static final Logger LOGGER = LoggerFactory.getLogger(StockTradingDaysGenerator.class);

	public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public static final List<LocalDate> EXCLUDED_DAYS = new ArrayList<>();
	public static final Map<Integer, Integer> DAYS_IN_MONTH = new LinkedHashMap<>();


	public static final List<String> ALL_DAYS = new ArrayList<>();
	public static final List<String> TRADE_DAYS = new ArrayList<>();
	public static final List<String> WORK_WEEK_DAYS = new ArrayList<>();
	static {
		DAYS_IN_MONTH.put(1, 31);
		DAYS_IN_MONTH.put(2, 29);
		DAYS_IN_MONTH.put(3, 31);
		DAYS_IN_MONTH.put(4, 30);
		DAYS_IN_MONTH.put(5, 31);
		DAYS_IN_MONTH.put(6, 30);
		DAYS_IN_MONTH.put(7, 31);
		DAYS_IN_MONTH.put(8, 31);
		DAYS_IN_MONTH.put(9, 30);
		DAYS_IN_MONTH.put(10, 31);
		DAYS_IN_MONTH.put(11, 30);
		DAYS_IN_MONTH.put(12, 31);

		EXCLUDED_DAYS.add(LocalDate.of(2024, 1, 1));
		EXCLUDED_DAYS.add(LocalDate.of(2025, 1, 1));


		initForLocalDate(LocalDate.of(2023, 12, 31));
		initForYear(2024);
		initForYear(2025);

		TRADE_DAYS.addAll(WORK_WEEK_DAYS);
		TRADE_DAYS.removeAll(EXCLUDED_DAYS);
		Collections.sort(TRADE_DAYS);
	}

	private static void initForLocalDate(LocalDate localDate) {
		String formattedExaminedDate = localDate.format(FORMATTER);
		ALL_DAYS.add(formattedExaminedDate);
		if (localDate.getDayOfWeek().getValue() != 6 && localDate.getDayOfWeek().getValue() != 7) {
			WORK_WEEK_DAYS.add(formattedExaminedDate);
		}
	}

	private static void initForYear(Integer year) {
		LocalDate examinedDate;
		String formattedExaminedDate;
		Integer dayOfMonth;
		for (Map.Entry<Integer, Integer> monthDays : DAYS_IN_MONTH.entrySet()) {
			dayOfMonth = 1;
			while (dayOfMonth <= monthDays.getValue()) {
				try {
					examinedDate = LocalDate.of(year, monthDays.getKey(), dayOfMonth);
					formattedExaminedDate = examinedDate.format(FORMATTER);
					ALL_DAYS.add(formattedExaminedDate);
					if (examinedDate.getDayOfWeek().getValue() != 6 && examinedDate.getDayOfWeek().getValue() != 7) {
						WORK_WEEK_DAYS.add(formattedExaminedDate);
					}
					dayOfMonth++;
				} catch (DateTimeException dte) {
					LOGGER.error("For examined date: {}-{}-{}", year, monthDays.getKey(), dayOfMonth);
					break;
				}
			}
		}
	}

	public List<String> generate() {
		return TRADE_DAYS;
	}
}
