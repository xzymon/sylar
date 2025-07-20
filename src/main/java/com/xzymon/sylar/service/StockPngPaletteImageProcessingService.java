package com.xzymon.sylar.service;

import com.xzymon.sylar.model.CsvOutput;
import com.xzymon.sylar.model.StqRawDataContainer;
import io.nayuki.png.image.BufferedPaletteImage;

public interface StockPngPaletteImageProcessingService {
	StqRawDataContainer extractRawDataFromImage(BufferedPaletteImage img);
	CsvOutput toCsvOutput(StqRawDataContainer container);
}
