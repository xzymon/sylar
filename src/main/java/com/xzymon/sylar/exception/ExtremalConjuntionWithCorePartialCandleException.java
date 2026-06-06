package com.xzymon.sylar.exception;

public class ExtremalConjuntionWithCorePartialCandleException extends PartialCandleException {
	public ExtremalConjuntionWithCorePartialCandleException(int partialCandleReferencePoint, int lastCoreReferencePoint) {
		super("Expected same position as last core, but got " + partialCandleReferencePoint + " instead of " + lastCoreReferencePoint);
	}
}
