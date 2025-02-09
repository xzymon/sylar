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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BarStockPngPaletteImageProcessingService_EURTRY20241225_Test {

	private BarStockPngPaletteImageProcessingService stockPngImageProcessingService = new BarStockPngPaletteImageProcessingService();

	private RawDataContainer rawDataContainer;

	private Map<Integer, NipponCandle> nipponCandles;
	private List<Integer> candleCoreIndices;

	private CsvOutput csvOutput;

	@BeforeAll
	void setUp() {
		ClassPathResource resource = new ClassPathResource("test-files/br_20241225.png");
		File imageFile = null;
		try {
			imageFile = resource.getFile();
			PngImage png = PngImage.read(imageFile);
			BufferedPaletteImage buffPalImg = (BufferedPaletteImage) ImageDecoder.toImage(png);
			rawDataContainer = stockPngImageProcessingService.extractRawDataFromImage(buffPalImg);
			assertNotNull(rawDataContainer);
			nipponCandles = RawDataContainerToNipponCandlesConverter.convert(rawDataContainer);
			assertNotNull(nipponCandles);
			candleCoreIndices = new ArrayList<>(nipponCandles.keySet().stream().toList());
			candleCoreIndices.sort(Comparator.naturalOrder());
			csvOutput = stockPngImageProcessingService.toCsvOutput(rawDataContainer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	void shouldDetect12Candles() {
		assertEquals(12, nipponCandles.size());
	}

	@Test
	void shouldDetectCandlesAtRightPlaces() {
		assertEquals(26, candleCoreIndices.get(0));
		assertEquals(27, candleCoreIndices.get(1));
		assertEquals(28, candleCoreIndices.get(2));
		assertEquals(29, candleCoreIndices.get(3));
		assertEquals(30, candleCoreIndices.get(4));
		assertEquals(31, candleCoreIndices.get(5));
		assertEquals(32, candleCoreIndices.get(6));
		assertEquals(33, candleCoreIndices.get(7));
		assertEquals(34, candleCoreIndices.get(8));
		assertEquals(92, candleCoreIndices.get(9));
		assertEquals(93, candleCoreIndices.get(10));
		assertEquals(94, candleCoreIndices.get(11));
	}
}
