package com.xzymon.sylar.helper;

public enum CmcCandlesStandardConfig {
	S422_CONFIG(1, 4, 2, 2),
	S423_CONFIG(1, 4, 2, 3),
	S522_CONFIG(1, 5, 2, 2);

	CmcCandlesStandardConfig(int pixelLenCoresGap, int pixelLenCoreLeftSide, int pixelLenCoreExtreme, int pixelLenCoreRightSide) {
		this.pixelLenCoresGap = pixelLenCoresGap;
		this.pixelLenCoreLeftSide = pixelLenCoreLeftSide;
		this.pixelLenCoreExtreme = pixelLenCoreExtreme;
		this.pixelLenCoreRightSide = pixelLenCoreRightSide;
	}

	int pixelLenCoresGap;		// długość w pikselach - odstęp pomiędzy rdzeniami świec
	// grubość trzonu = pixelLenCoreLeftSide + pixelLenCoreExtreme + pixelLenCoreRightSide
	int pixelLenCoreLeftSide;	// długość w pikselach - od lewego brzegu trzonu do knota
	int pixelLenCoreExtreme;	// długość w pikselach - grubość knota
	int pixelLenCoreRightSide;	// długość w pikselach - od knota do prawego brzegu
}
