package com.xzymon.sylar.constants;

public enum ChartType {
	BAR("Interwal 15 Min."),
	LINE("Interwal 5 Min.");

	private String chartTypeName;

	ChartType(String chartTypeName) {
		this.chartTypeName = chartTypeName;
	}

	public String getChartTypeName() {
		return chartTypeName;
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
