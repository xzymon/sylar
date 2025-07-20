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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BarStockPngPaletteImageProcessingService_SOL_V_20240728_Test {

	private TradingDaysGeneratorService tradingDaysGeneratorService = new CryptoTradingDaysGeneratorService();
	private NipponCandlesConversionService nipponCandlesConversionService = new NipponCandlesConversionService(tradingDaysGeneratorService);
	private BarStockPngPaletteImageProcessingService stockPngImageProcessingService = new BarStockPngPaletteImageProcessingService(nipponCandlesConversionService);

	private StqRawDataContainer rawDataContainer;

	private Map<Integer, NipponCandle> nipponCandles;
	private List<Integer> candleCoreIndices;

	private CsvOutput csvOutput;

	@BeforeAll
	void setUp() {
		ClassPathResource resource = new ClassPathResource("test-files/SOL.V_br_20240728.png");
		File imageFile = null;
		try {
			imageFile = resource.getFile();
			PngImage png = PngImage.read(imageFile);
			BufferedPaletteImage buffPalImg = (BufferedPaletteImage) ImageDecoder.toImage(png);
			rawDataContainer = stockPngImageProcessingService.extractRawDataFromImage(buffPalImg);
			assertNotNull(rawDataContainer);
			nipponCandles = nipponCandlesConversionService.convert(rawDataContainer);
			assertNotNull(nipponCandles);
			candleCoreIndices = new ArrayList<>(nipponCandles.keySet().stream().toList());
			candleCoreIndices.sort(Comparator.naturalOrder());
			csvOutput = stockPngImageProcessingService.toCsvOutput(rawDataContainer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	void shouldDetectDayOpenLine() {
		String previousDayCloseLine = csvOutput.getPreviousDayCloseLine();
		String[] splitted = previousDayCloseLine.split(",");
		assertEquals("183.0", splitted[2]);
	}
}
