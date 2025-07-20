package com.xzymon.sylar.service;

import com.xzymon.sylar.model.CsvOutput;
import com.xzymon.sylar.model.NipponCandle;
import com.xzymon.sylar.model.StqRawDataContainer;
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
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BarStockPngPaletteImageProcessingService_USDJPY20241011_Test {

	private TradingDaysGeneratorService tradingDaysGeneratorService = new StockTradingDaysGeneratorService();
	private NipponCandlesConversionService nipponCandlesConversionService = new NipponCandlesConversionService(tradingDaysGeneratorService);
	private BarStockPngPaletteImageProcessingService stockPngImageProcessingService = new BarStockPngPaletteImageProcessingService(nipponCandlesConversionService);

	private StqRawDataContainer rawDataContainer;

	private Map<Integer, NipponCandle> nipponCandles;

	private CsvOutput csvOutput;

	@BeforeAll
	void setUp() {
		ClassPathResource resource = new ClassPathResource("test-files/br_20241011.png");
		File imageFile = null;
		try {
			imageFile = resource.getFile();
			PngImage png = PngImage.read(imageFile);
			BufferedPaletteImage buffPalImg = (BufferedPaletteImage) ImageDecoder.toImage(png);
			rawDataContainer = stockPngImageProcessingService.extractRawDataFromImage(buffPalImg);
			assertNotNull(rawDataContainer);
			nipponCandles = nipponCandlesConversionService.convert(rawDataContainer);
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

	@Test
	void shouldDetectFirstVerticalGaugeAt70() {
		List<Integer> keysList = rawDataContainer.getVerticalGauges().keySet().stream().toList();
		List<Integer> orderedList = keysList.stream().sorted().toList();
		assertEquals(70, orderedList.get(0));
	}
}
