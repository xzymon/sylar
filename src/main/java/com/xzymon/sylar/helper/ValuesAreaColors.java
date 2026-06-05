package com.xzymon.sylar.helper;

public enum ValuesAreaColors {
	ASCENDING_CORE(0xff00e000),		// zielony jasny // kolor rdzenia świecy wzrostowej
	ASCENDING_EXTREME(0xff00b200),	// zielony ciemny // kolor knota świecy wzrostowej
	DESCENDING_CORE(0xffff0000),	// pełny czerwony // kolor rdzenia świecy spadkowej
	DESCENDING_EXTREME(0xffca0000),	// ciemniejszy czerwony // kolor knota świecy spadkowej

	// poniżej: kolor gdy knot świecy jest ponad prowadnicą (pionową lub poziomą)
	GAUGE_ASCENDING_EXTREME(0xff0bbd0b),
	GAUGE_DESCENDING_EXTREME(0xffd60b0b),
	// dla trzonów powyższe zjawisko nie zachodzi - tzn. trzon nad prowadnicą ma po prostu kolor trzonu

	// poniżej: kolor gdy linia bieżącej (tzn. najbardziej "na prawo") wartości (ta "biała") jest ponad knotem lub trzonem świecy
	CURRENT_LEVEL_ASCENDING_CORE(0xffcaf9ca),
	CURRENT_LEVEL_ASCENDING_EXTREME(0xffcaefca),
	CURRENT_LEVEL_DESCENDING_CORE(0xffffcaca),
	CURRENT_LEVEL_DESCENDING_EXTREME(0xfff4caca);

	ValuesAreaColors(int color) {
		this.color = color;
	}

	int color;

	public int getColor() {
		return color;
	}
}
