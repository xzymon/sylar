package com.xzymon.sylar.constants;

public enum ChartType {
	BAR("Interwal 15 Min.", 24 * (60 / 15) - 1),
	LINE("Interwal 5 Min.", 24 * (60 / 5));

	private String chartTypeName;
	private int expectedValuePointsCount;

	ChartType(String chartTypeName, int expectedValuePointsCount) {
		this.chartTypeName = chartTypeName;
		this.expectedValuePointsCount = expectedValuePointsCount;
	}

	public String getChartTypeName() {
		return chartTypeName;
	}

	public int getExpectedValuePointsCount() {
		return expectedValuePointsCount;
	}

	public static ChartType getChartType(String chartTypeName) {
		for(ChartType chartType : ChartType.values()) {
			if(chartType.getChartTypeName().equals(chartTypeName)) {
				return chartType;
			}
		}
		return null;
	}
}
