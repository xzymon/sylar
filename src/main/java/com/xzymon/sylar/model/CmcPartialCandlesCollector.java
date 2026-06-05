package com.xzymon.sylar.model;

import com.xzymon.sylar.exception.PartialCandleColorMismatch;

import java.util.List;

/**
 * Represents a combined candle constructed from partial candles.
 *
 * To obiekt pośredni pomiędzy zczytanymi pionowymi paskami z wykresu (CmcPartialCandle) a RawDataNipponCandle.
 *
 * Ważne założenie:
 * - przyjmuje się, że pojedynczy CmcPartialCandle jest zerowej grubości (choć tak naprawdę jego grubość to 1), ale
 */
public class CmcPartialCandlesCollector {
	private List<CmcPartialCandle> coreCandles;
	private List<CmcPartialCandle> extremalCandles;

	private RawDataNipponCandle rawDataNipponCandle;
	private FrameCoords coreFC;
	private FrameCoords extremalFC;
	private FrameCoords absoluteFC;
	private boolean isAscending;

	public CmcPartialCandlesCollector(CmcPartialCandle partialCandle) {
		this.coreCandles = new java.util.ArrayList<>();
		this.extremalCandles = new java.util.ArrayList<>();
		init(partialCandle);
	}

	private void init(CmcPartialCandle partialCandle) {
		switch (partialCandle.getColor()) {
			case ASCENDING_CORE:
				this.isAscending = true;
				this.coreCandles.add(partialCandle);
				break;
			case ASCENDING_EXTREME:
				this.isAscending = true;
				this.extremalCandles.add(partialCandle);
				break;
			case DESCENDING_CORE:
				this.isAscending = false;
				this.coreCandles.add(partialCandle);
				break;
			case DESCENDING_EXTREME:
				this.isAscending = false;
				break;
			default:
				throw new IllegalArgumentException("Unsupported color: " + partialCandle.getColor());
		}
	}

	public CmcPartialCandle getInitialPartialCandle() {
		return coreCandles.get(0);
	}

	/*
	public boolean join(CmcPartialCandle partialCandle) {
		if (partialCandle.getColor() != coreCandles.get(0).getColor()) {
			//throw new PartialCandleColorMismatch()
		}
		coreCandles.add(partialCandle);
	}*/
}
