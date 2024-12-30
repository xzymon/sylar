package com.xzymon.sylar.service;

import com.xzymon.sylar.model.CsvOutput;
import com.xzymon.sylar.model.RawDataContainer;
import io.nayuki.png.image.BufferedPaletteImage;

public interface StockPngPaletteImageProcessingService {
	RawDataContainer extractRawDataFromImage(BufferedPaletteImage img);
	CsvOutput toCsvOutput(RawDataContainer container);
}
