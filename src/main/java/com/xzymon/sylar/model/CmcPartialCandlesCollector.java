package com.xzymon.sylar.model;

import com.xzymon.sylar.exception.*;
import com.xzymon.sylar.helper.ValuesAreaColors;

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
	private boolean ascendingCollector;

	public CmcPartialCandlesCollector(CmcPartialCandle partialCandle) {
		this.coreCandles = new java.util.ArrayList<>();
		this.extremalCandles = new java.util.ArrayList<>();
		init(partialCandle);
	}

	private void init(CmcPartialCandle partialCandle) {
		//NOTE: to ważne: zdecydowałem że nie można utworzyć CmcCombinedFromPartialCandle z CmcPartialCandle o innym kolorze niż ASCENDING_CORE lub DESCENDING_CORE
		// to upraszcza logikę, a poza tym powinno być wygodne ze względu na logikę stosowaną w CmcPartialCandlePresenceRegister
		switch (partialCandle.getColor()) {
			case ASCENDING_CORE:
				this.ascendingCollector = true;
				this.coreCandles.add(partialCandle);
				break;
			case DESCENDING_CORE:
				this.ascendingCollector = false;
				this.coreCandles.add(partialCandle);
				break;
			default:
				throw new IllegalArgumentException("Cant init candle with color: " + partialCandle.getColor());
		}
	}

	public CmcPartialCandle getInitialPartialCandle() {
		return coreCandles.get(0);
	}

	public void join(CmcPartialCandle partialCandle) {
		if (ascendingCollector) {
			if (!isColorAscending(partialCandle.getColor())) {
				throw new ColorMismatchPartialCandleException("Expected ascending color, but got " + partialCandle.getColor());
			}
		}
		if (!ascendingCollector) {
			if (!isColorDescending(partialCandle.getColor())) {
				throw new ColorMismatchPartialCandleException("Expected descending color, but got " + partialCandle.getColor());
			}
		}
		switch (partialCandle.getColor()) {
			case ASCENDING_CORE:
			case DESCENDING_CORE:
				checkNextToLastCore(partialCandle);
				checkSameSizeAsInitialCore(partialCandle);
				coreCandles.add(partialCandle);
				break;
			case ASCENDING_EXTREME:
			case DESCENDING_EXTREME:
				if (extremalCandles.isEmpty()) {
					checkSamePositionAsLastCore(partialCandle);
				} else {
					checkNextToLastExtremal(partialCandle);
					checkSameSizeAsFirstExtremal(partialCandle);
				}
				extremalCandles.add(partialCandle);
				break;
			default:
				throw new IllegalArgumentException("Unsupported color: " + partialCandle.getColor());
		}
	}

	private void checkSamePositionAsLastCore(CmcPartialCandle partialCandle) {
		CmcPartialCandle lastCore = coreCandles.get(coreCandles.size() - 1);
		int lastCoreReferencePoint = lastCore.getReferencePointOnAxis();
		if (partialCandle.getReferencePointOnAxis() != lastCoreReferencePoint) {
			throw new ExtremalConjuntionWithCorePartialCandleException(partialCandle.getReferencePointOnAxis(), lastCoreReferencePoint);
		}
	}

	private void checkNextToLastCore(CmcPartialCandle partialCandle) {
		CmcPartialCandle lastCore = coreCandles.get(coreCandles.size() - 1);
		int lastCoreReferencePoint = lastCore.getReferencePointOnAxis();
		int partialCandleReferencePoint = partialCandle.getReferencePointOnAxis();
		if (partialCandleReferencePoint != lastCoreReferencePoint + 1) {
			throw new CoreLocationPartialCandleException("Expected next to last core, but got " + partialCandleReferencePoint + " instead of " + (lastCoreReferencePoint + 1));
		}
	}

	private void checkNextToLastExtremal(CmcPartialCandle partialCandle) {
		CmcPartialCandle lastExtremal = extremalCandles.get(extremalCandles.size() - 1);
		int lastExtremalReferencePoint = lastExtremal.getReferencePointOnAxis();
		int partialCandleReferencePoint = partialCandle.getReferencePointOnAxis();
		if (partialCandleReferencePoint != lastExtremalReferencePoint + 1) {
			throw new ExtremalLocationPartialCandleException("Expected next to last extremal, but got " + partialCandleReferencePoint + " instead of " + (lastExtremalReferencePoint + 1));
		}
	}

	private void checkSameSizeAsInitialCore(CmcPartialCandle partialCandle) {
		CmcPartialCandle initialCore = coreCandles.get(0);
		int initialCoreTop = initialCore.getMin();
		int initialCoreBottom = initialCore.getMax();
		int partialCandlePosition = partialCandle.getReferencePointOnAxis();
		int partialCandleTop = partialCandle.getMin();
		int partialCandleBottom = partialCandle.getMax();

		if (partialCandleTop != initialCoreTop || partialCandleBottom != initialCoreBottom) {
			throw new SizePartialCandleException("Expected same size as initial core, but it differs. Position: [" + partialCandlePosition + "]. Initial core <top;bottom>=<" + initialCoreTop + ";" + initialCoreBottom + ">. Candle in check core=<" + partialCandleTop + ";" + partialCandleBottom + ">");
		}
	}

	private void checkSameSizeAsFirstExtremal(CmcPartialCandle partialCandle) {
		CmcPartialCandle lastExtremal = extremalCandles.get(extremalCandles.size() - 1);
		int lastExtremalTop = lastExtremal.getMin();
		int lastExtremalBottom = lastExtremal.getMax();
		int partialCandlePosition = partialCandle.getReferencePointOnAxis();
		int partialCandleTop = partialCandle.getMin();
		int partialCandleBottom = partialCandle.getMax();

		if (partialCandleTop != lastExtremalTop || partialCandleBottom != lastExtremalBottom) {
			throw new SizePartialCandleException("Expected same size as first extremal, but it differs. Position: [" + partialCandlePosition + "]. First extremal <top;bottom>=<" + lastExtremalTop + ";" + lastExtremalBottom + ">. Candle in check extremal=<" + partialCandleTop + ";" + partialCandleBottom + ">");
		}
	}

	public boolean isColorAscending(ValuesAreaColors color) {
		return color == ValuesAreaColors.ASCENDING_CORE || color == ValuesAreaColors.ASCENDING_EXTREME;
	}

	public boolean isColorDescending(ValuesAreaColors color) {
		return color == ValuesAreaColors.DESCENDING_CORE || color == ValuesAreaColors.DESCENDING_EXTREME;
	}
}
