package com.xzymon.sylar.model;

import java.math.BigDecimal;

public class NipponCandle {
	private String dateString;
	private String timeString;
	private BigDecimal open;
	private BigDecimal high;
	private BigDecimal low;
	private BigDecimal close;

	public NipponCandle() {
	}

	public NipponCandle(String dateString, String timeString, BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close) {
		super();
		this.dateString = dateString;
		this.timeString = timeString;
	}

	public String getDateString() {
		return dateString;
	}

	public void setDateString(String dateString) {
		this.dateString = dateString;
	}

	public String getTimeString() {
		return timeString;
	}

	public void setTimeString(String timeString) {
		this.timeString = timeString;
	}

	public BigDecimal getOpen() {
		return open;
	}

	public void setOpen(BigDecimal open) {
		this.open = open;
	}

	public BigDecimal getHigh() {
		return high;
	}

	public void setHigh(BigDecimal high) {
		this.high = high;
	}

	public BigDecimal getLow() {
		return low;
	}

	public void setLow(BigDecimal low) {
		this.low = low;
	}

	public BigDecimal getClose() {
		return close;
	}

	public void setClose(BigDecimal close) {
		this.close = close;
	}

	@Override
	public String toString() {
		return "NipponCandleInterpretation{" +
				       "dateString='" + dateString + '\'' +
				       ", timeString='" + timeString + '\'' +
				       ", open=" + open +
				       ", high=" + high +
				       ", low=" + low +
				       ", close=" + close +
				       '}';
	}

	public String toCsvRow() {
		return String.format("%1$s,%2$s,%3$s,%4$s,%5$s,%6$s", dateString, timeString, open, high, low, close);
	}
}
