package com.xzymon.sylar.helper;

public enum ValuesAreaColors {
	ASCENDING_CORE(0xff00e000),		// zielony jasny // kolor rdzenia świecy wzrostowej
	ASCENDING_EXTREME(0xff00b200),	// zielony ciemny // kolor knota świecy wzrostowej
	DESCENDING_CORE(0xffff0000),	// pełny czerwony // kolor rdzenia świecy spadkowej
	DESCENDING_EXTREME(0xffca0000),	// ciemniejszy czerwony // kolor knota świecy spadkowej

	// gdy trzon świecy jest nad prowadnicą - to zachowuje dokładnie ten sam kolor - kolor trzonu
	// najwyraźniej trzon świecy ma kanał alfa z wartością 0 (ja wiem - to nie wynika z obecnych tu definicji kolorów - ale to jest już obserwowalny efekt - a nie bebechy aplikacji CMC)
	// natomiast gdy knot świecy jest nad prowadnicą - wtedy kolor prowadnicy "przebija" ten kolor (tak jakby jej kanał alfa był <> 0) - i powstaje nowy kolor
	// ten powstały nowy kolor w takiej sytuacji też trzeba uwzględnić przy zczytywaniu obecności świecy
	// zatem poniżej: kolor gdy knot świecy jest ponad prowadnicą (pionową lub poziomą)
	GAUGE_ASCENDING_EXTREME(0xff0bbd0b),
	GAUGE_DESCENDING_EXTREME(0xffd60b0b),
	// jest jeszcze kolejny poziom tego zjawiska - tzn. gdy 2 prowadnice (pozioma i pionowa) się przetną - a nad nią jest knot
	CROSSING_GAUGE_ASCENDING_EXTREME(0xff13c513),
	CROSSING_GAUGE_DESCENDING_EXTREME(0xffdd1313),

	// linia bieżącego poziomu ceny (czyli tego dotyczącego świecy najbardziej "na prawo" - która jest aktywnie zmieniana)
	CURRENT_LEVEL(0xffcacaca),
	// ta linia ma najwyższy Z-index (jest najwyżej - tzn. najbliżej obserwatora) - i na niej też zachodzi przebijanie ze świec (jej alfa jest <> 0)
	// poniżej: kolor gdy linia bieżącej (tzn. najbardziej "na prawo") wartości (ta "biała") jest ponad knotem lub trzonem świecy
	CURRENT_LEVEL_ASCENDING_CORE(0xffcaf9ca),
	CURRENT_LEVEL_ASCENDING_EXTREME(0xffcaefca),
	CURRENT_LEVEL_DESCENDING_CORE(0xffffcaca),
	CURRENT_LEVEL_DESCENDING_EXTREME(0xfff4caca),
	//kolejny poziom: gdy linia bieżącego poziomu jest nad knotem świecy - a ten jest nad prowadnicą pionową
	CURRENT_LEVEL_GAUGE_ASCENDING_EXTREME(0xffcdf1cd),
	CURRENT_LEVEL_GAUGE_DESCENDING_EXTREME(0xfff7cdcd);


	ValuesAreaColors(int color) {
		this.color = color;
	}

	int color;

	public int getColor() {
		return color;
	}
}
