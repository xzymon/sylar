package com.xzymon.sylar.helper;

import com.xzymon.sylar.model.CmcPartialCandle;

public class CmcPartialCandlePrescenceRegister {
	private CmcPartialCandlePresence[] candlePrescence;
	private PresenceStats presenceStats;

	public CmcPartialCandlePrescenceRegister(int length) {
		this.candlePrescence = new CmcPartialCandlePresence[length];
	}

	public void put(CmcPartialCandle candle) {
		if (candle == null) {
			throw new IllegalArgumentException("Candle cannot be null");
		}
		Integer referencePoint = candle.getReferencePointOnAxis();
		if (referencePoint < 0) {
			throw new IllegalArgumentException("Candle reference point must be non-negative");
		}
		if (referencePoint >= candlePrescence.length) {
			throw new IllegalArgumentException("Candle reference point must be less than candlePrescence.length");
		}
		if (candlePrescence[referencePoint] == null) {
			candlePrescence[referencePoint] = new CmcPartialCandlePresence();
		}
		CmcPartialCandlePresence candlePresenceAtReferencePoint = candlePrescence[referencePoint];
		switch (candle.getColor()) {
			case ASCENDING_CORE:
				candlePresenceAtReferencePoint.setAscendingCore(candle);
				break;
			case ASCENDING_EXTREME:
				candlePresenceAtReferencePoint.setAscendingExtreme(candle);
				break;
			case DESCENDING_CORE:
				candlePresenceAtReferencePoint.setDescendingCore(candle);
				break;
			case DESCENDING_EXTREME:
				candlePresenceAtReferencePoint.setDescendingExtreme(candle);
				break;
		}
	}

	public CmcPartialCandle getAscendingCore(int referencePoint) {
		if (referencePoint < 0 || referencePoint >= candlePrescence.length) {
			return null;
		}
		if (candlePrescence[referencePoint] == null) {
			return null;
		}
		return candlePrescence[referencePoint].getAscendingCore();
	}

	public CmcPartialCandle getAscendingExtreme(int referencePoint) {
		if (referencePoint < 0 || referencePoint >= candlePrescence.length) {
			return null;
		}
		if (candlePrescence[referencePoint] == null) {
			return null;
		}
		return candlePrescence[referencePoint].getAscendingExtreme();
	}

	public CmcPartialCandle getDescendingCore(int referencePoint) {
		if (referencePoint < 0 || referencePoint >= candlePrescence.length) {
			return null;
		}
		if (candlePrescence[referencePoint] == null) {
			return null;
		}
		return candlePrescence[referencePoint].getDescendingCore();
	}

	public CmcPartialCandle getDescendingExtreme(int referencePoint) {
		if (referencePoint < 0 || referencePoint >= candlePrescence.length) {
			return null;
		}
		if (candlePrescence[referencePoint] == null) {
			return null;
		}
		return candlePrescence[referencePoint].getDescendingExtreme();
	}

	public PresenceStats calculateStats() {
		PresenceStats stats = new PresenceStats();
		for (int i = 0; i < candlePrescence.length; i++) {
			CmcPartialCandlePresence candlePresence = candlePrescence[i];
			if (candlePresence == null) {
				stats.empty++;
			} else {
				stats.notEmpty++;
				if (candlePresence.getAscendingCore() != null) {
					stats.ascendingCore++;
				}
				if (candlePresence.getDescendingCore() != null) {
					stats.descendingCore++;
				}
				if (candlePresence.getAscendingCore() != null && candlePresence.getAscendingExtreme() != null) {
					stats.bothAscending++;
				}
				if (candlePresence.getAscendingExtreme() != null) {
					stats.ascendingExtreme++;
				}
				if (candlePresence.getDescendingExtreme() != null) {
					stats.descendingExtreme++;
				}
				if (candlePresence.getDescendingCore() != null && candlePresence.getDescendingExtreme() != null) {
					stats.bothDescending++;
				}
			}
		}
		this.presenceStats = stats;
		return stats;
	}

	public PresenceStats getPresenceStats() {
		return presenceStats;
	}

	public boolean isCorrect() {
		return presenceStats.ascendingExtreme == presenceStats.bothAscending && presenceStats.descendingExtreme == presenceStats.bothDescending;
	}

	public String getStats() {
		PresenceStats stats = calculateStats();
		return String.format("Empty: %d, NotEmpty: %d, AscendingCore: %d, AscendingExtreme: %d, BothAscending: %d, DescendingCore: %d, DescendingExtreme: %d, BothDescending: %d",
				stats.empty, stats.notEmpty, stats.ascendingCore, stats.ascendingExtreme, stats.bothAscending, stats.descendingCore, stats.descendingExtreme, stats.bothDescending);
	}

	public static class PresenceStats {
		private int empty;
		private int notEmpty;
		private int ascendingCore;
		private int descendingCore;
		private int ascendingExtreme;
		private int descendingExtreme;
		private int bothAscending;
		private int bothDescending;

		public int getEmpty() {
			return empty;
		}

		public int getNotEmpty() {
			return notEmpty;
		}

		public int getAscendingCore() {
			return ascendingCore;
		}

		public int getDescendingCore() {
			return descendingCore;
		}

		public int getAscendingExtreme() {
			return ascendingExtreme;
		}

		public int getDescendingExtreme() {
			return descendingExtreme;
		}

		public int getBothAscending() {
			return bothAscending;
		}

		public int getBothDescending() {
			return bothDescending;
		}
	}
}
