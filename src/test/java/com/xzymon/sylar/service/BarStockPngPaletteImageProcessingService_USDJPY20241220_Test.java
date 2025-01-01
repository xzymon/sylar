package com.xzymon.sylar.service;

import com.xzymon.sylar.model.CsvOutput;
import com.xzymon.sylar.model.NipponCandle;
import com.xzymon.sylar.model.RawDataContainer;
import com.xzymon.sylar.processing.RawDataContainerToNipponCandlesConverter;
import io.nayuki.png.ImageDecoder;
import io.nayuki.png.PngImage;
import io.nayuki.png.image.BufferedPaletteImage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BarStockPngPaletteImageProcessingService_USDJPY20241220_Test {

	private BarStockPngPaletteImageProcessingService stockPngImageProcessingService = new BarStockPngPaletteImageProcessingService();

	private RawDataContainer rawDataContainer;

	private Map<Integer, NipponCandle> nipponCandles;

	private CsvOutput csvOutput;

	@BeforeAll
	void setUp() {
		ClassPathResource resource = new ClassPathResource("test-files/br_20241220.png");
		File imageFile = null;
		try {
			imageFile = resource.getFile();
			PngImage png = PngImage.read(imageFile);
			BufferedPaletteImage buffPalImg = (BufferedPaletteImage) ImageDecoder.toImage(png);
			rawDataContainer = stockPngImageProcessingService.extractRawDataFromImage(buffPalImg);
			assertNotNull(rawDataContainer);
			nipponCandles = RawDataContainerToNipponCandlesConverter.convert(rawDataContainer);
			assertNotNull(nipponCandles);
			csvOutput = stockPngImageProcessingService.toCsvOutput(rawDataContainer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	void shouldDetect89Candles() {
		assertEquals(89, nipponCandles.size());
	}
}
