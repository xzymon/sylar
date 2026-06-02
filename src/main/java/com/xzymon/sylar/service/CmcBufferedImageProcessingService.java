package com.xzymon.sylar.service;

import com.xzymon.sylar.model.CmcRawDataContainer;
import com.xzymon.sylar.model.CsvOutput;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

public interface CmcBufferedImageProcessingService {
	CmcRawDataContainer extractRawDataFromImage(BufferedImage image, Path inputPath) throws IOException;
	CsvOutput toCsvOutput(CmcRawDataContainer container);
}
