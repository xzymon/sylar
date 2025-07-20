package com.xzymon.sylar.constants;

public enum StqChartType {
	BAR("Interwal 15 Min.", 24 * (60 / 15) - 1, "4:00", "24:00", 80),
	LINE("Interwal 5 Min.", 24 * (60 / 5), "4:00", "24:00", 240);

	private String chartTypeName;
	private int expectedValuePointsCount;
	private String firstAlternativeTimePointText;
	private String lastAlternativeTimePointText;
	private int betweenTimePointsCount;

	StqChartType(String chartTypeName, int expectedValuePointsCount, String firstAlternativeTimePointText, String lastAlternativeTimePointText, int betweenTimePointsCount) {
		this.chartTypeName = chartTypeName;
		this.expectedValuePointsCount = expectedValuePointsCount;
		this.firstAlternativeTimePointText = firstAlternativeTimePointText;
		this.lastAlternativeTimePointText = lastAlternativeTimePointText;
		this.betweenTimePointsCount = betweenTimePointsCount;
	}

	public String getChartTypeName() {
		return chartTypeName;
	}

	public int getExpectedValuePointsCount() {
		return expectedValuePointsCount;
	}

	public String getFirstAlternativeTimePointText() {
		return firstAlternativeTimePointText;
	}

	public String getLastAlternativeTimePointText() {
		return lastAlternativeTimePointText;
	}

	public int getBetweenTimePointsCount() {
		return betweenTimePointsCount;
	}

	public static StqChartType getChartType(String chartTypeName) {
		for(StqChartType stqChartType : StqChartType.values()) {
			if(stqChartType.getChartTypeName().equals(chartTypeName)) {
				return stqChartType;
			}
		}
		return null;
	}
}
