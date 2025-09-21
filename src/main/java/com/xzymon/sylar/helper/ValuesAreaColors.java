package com.xzymon.sylar.helper;

public enum ValuesAreaColors {
	ASCENDING_CORE(0xFF00E000),		// zielony jasny // kolor rdzenia świecy wzrostowej
	ASCENDING_EXTREME(0xFF00B200),	// zielony ciemny // kolor knota świecy wzrostowej
	DESCENDING_CORE(0xFFFF0000),	// pełny czerwony // kolor rdzenia świecy spadkowej
	DESCENDING_EXTREME(0xFFCA0000);	// ciemniejszy czerwony // kolor knota świecy spadkowej

	ValuesAreaColors(int color) {
		this.color = color;
	}

	int color;

	public int getColor() {
		return color;
	}
}
