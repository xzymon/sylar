package com.xzymon.sylar.constants;

public enum Interval {
	ONE_SECOND("1s", "01s", 1),
	FIVE_SECONDS("5s", "05s", 5),
	TEN_SECONDS("10s", "10s", 10),
	FIFTEEN_SECONDS("15s", "15s", 15),
	THIRTY_SECONDS("30s", "30s", 30),
	ONE_MINUTE("1m", "01m", 60),
	TWO_MINUTES("2m", "02m", 120),
	FIVE_MINUTES("5m", "05m", 300),
	TEN_MINUTES("10m", "10m", 600),
	FIFTEEN_MINUTES("15m", "15m", 900),
	THIRTY_MINUTES("30m", "30m", 1800),
	ONE_HOUR("1h", "01h", 3600),
	TWO_HOURS("2h", "02h", 7200),
	FOUR_HOURS("4h", "04h", 14400),
	EIGHT_HOURS("8h", "08h", 28800),
	ONE_DAY("1d", "01d", 86400);

	private String name;
	private String fileNameComponent;
	private int timeInSeconds;

	Interval(String name, String fileNameComponent, int timeInSeconds) {
		this.name = name;
		this.fileNameComponent = fileNameComponent;
		this.timeInSeconds = timeInSeconds;
	}

	public static Interval getIntervalByName(String name) {
		for(Interval interval : Interval.values()) {
			if(interval.getName().equals(name)) {
				return interval;
			}
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public String getFileNameComponent() {
		return fileNameComponent;
	}

	public int getTimeInSeconds() {
		return timeInSeconds;
	}

	@Override
	public String toString() {
		return name;
	}
}
