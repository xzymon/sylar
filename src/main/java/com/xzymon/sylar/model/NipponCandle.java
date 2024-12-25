package com.xzymon.sylar.model;

import java.util.ArrayList;
import java.util.List;

public class NipponCandle {
	private int datetimeMarker;
	private int open;
	private int high;
	private int low;
	private int close;

	public NipponCandle() {

	}

	public NipponCandle(int datetimeMarker, int open, int high, int low, int close) {
		this.datetimeMarker = datetimeMarker;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
	}

	public int getDatetimeMarker() {
		return datetimeMarker;
	}

	public void setDatetimeMarker(int datetimeMarker) {
		this.datetimeMarker = datetimeMarker;
	}

	public int getOpen() {
		return open;
	}

	public void setOpen(int open) {
		this.open = open;
	}

	public int getHigh() {
		return high;
	}

	public void setHigh(int high) {
		this.high = high;
	}

	public int getLow() {
		return low;
	}

	public void setLow(int low) {
		this.low = low;
	}

	public int getClose() {
		return close;
	}

	public void setClose(int close) {
		this.close = close;
	}

	@Override
	public String toString() {
		return "NipponCandle{" +
				       "datetimeMarker=" + datetimeMarker +
				       ", open=" + open +
				       ", high=" + high +
				       ", low=" + low +
				       ", close=" + close +
				       '}';
	}

	public static class BuildingBlock {
		private List<RawValueInBuckets> rawValues = new ArrayList<>();
		private int datetimeMarker;

		public BuildingBlock() {
		}

		public List<RawValueInBuckets> getRawValues() {
			return rawValues;
		}

		public void setRawValues(List<RawValueInBuckets> rawValues) {
			this.rawValues = rawValues;
		}

		public int getDatetimeMarker() {
			return datetimeMarker;
		}

		public void setDatetimeMarker(int datetimeMarker) {
			this.datetimeMarker = datetimeMarker;
		}
	}
}
