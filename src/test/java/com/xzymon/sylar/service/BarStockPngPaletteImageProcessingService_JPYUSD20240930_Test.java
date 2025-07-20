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
import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BarStockPngPaletteImageProcessingService_JPYUSD20240930_Test {

	private TradingDaysGeneratorService tradingDaysGeneratorService = new StockTradingDaysGeneratorService();
	private NipponCandlesConversionService nipponCandlesConversionService = new NipponCandlesConversionService(tradingDaysGeneratorService);
	private BarStockPngPaletteImageProcessingService stockPngImageProcessingService = new BarStockPngPaletteImageProcessingService(nipponCandlesConversionService);

	private StqRawDataContainer rawDataContainer;

	private Map<Integer, NipponCandle> nipponCandles;

	private CsvOutput csvOutput;

	@BeforeAll
	void setUp() {
		ClassPathResource resource = new ClassPathResource("test-files/br_20240930.png");
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
	void shouldDetect95Candles() {
		assertEquals(95, nipponCandles.size());
	}

	@Test
	void shouldDetectValorNameAreaText() {
		assertEquals("JPYUSD - 1 dzien", rawDataContainer.getValorNameArea().getExtractedText().trim());
	}

	@Test
	void shouldDetectGeneratedDateTimeAreaText() {
		assertEquals("30 Wrz 2024 23:59 CEST", rawDataContainer.getGeneratedDateTimeArea().getExtractedText().trim());
	}

	@Test
	void shouldDetectIntervalAreaText() {
		assertEquals("Interwal 15 Min.", rawDataContainer.getIntervalArea().getExtractedText().trim());
	}

	@Test
	void shouldRetainSameValuesForNipponCandle_0() {
		NipponCandle nipponCandle = nipponCandles.get(0);
		assertEquals("2024-09-30", nipponCandle.getDateString());
		assertEquals("00:15:00", nipponCandle.getTimeString());
		assertEquals(new BigDecimal("0.00702841"), nipponCandle.getOpen());
		assertEquals(new BigDecimal("0.00702977"), nipponCandle.getHigh());
		assertEquals(new BigDecimal("0.00701477"), nipponCandle.getLow());
		assertEquals(new BigDecimal("0.00701682"), nipponCandle.getClose());
	}

	@Test
	void shouldRetainSameValuesForNipponCandle_32() {
		NipponCandle nipponCandle = nipponCandles.get(32);
		assertEquals("2024-09-30", nipponCandle.getDateString());
		assertEquals("08:15:00", nipponCandle.getTimeString());
		assertEquals(new BigDecimal("0.00705682"), nipponCandle.getOpen());
		assertEquals(new BigDecimal("0.00705955"), nipponCandle.getHigh());
		assertEquals(new BigDecimal("0.00704750"), nipponCandle.getLow());
		assertEquals(new BigDecimal("0.00704864"), nipponCandle.getClose());
	}

	@Test
	void shouldRetainSameValuesForNipponCandle_80() {
		NipponCandle nipponCandle = nipponCandles.get(80);
		assertEquals("2024-09-30", nipponCandle.getDateString());
		assertEquals("20:15:00", nipponCandle.getTimeString());
		assertEquals(new BigDecimal("0.00698841"), nipponCandle.getOpen());
		assertEquals(new BigDecimal("0.00698841"), nipponCandle.getHigh());
		assertEquals(new BigDecimal("0.00696091"), nipponCandle.getLow());
		assertEquals(new BigDecimal("0.00696477"), nipponCandle.getClose());
	}

	@Test
	void shouldRetainSameValuesForNipponCandle_81() {
		NipponCandle nipponCandle = nipponCandles.get(81);
		assertEquals("2024-09-30", nipponCandle.getDateString());
		assertEquals("20:30:00", nipponCandle.getTimeString());
		assertEquals(new BigDecimal("0.00696477"), nipponCandle.getOpen());
		assertEquals(new BigDecimal("0.00696477"), nipponCandle.getHigh());
		assertEquals(new BigDecimal("0.00694864"), nipponCandle.getLow());
		assertEquals(new BigDecimal("0.00695205"), nipponCandle.getClose());
	}

	@Test
	void shouldRetainSameValuesForNipponCandle_86() {
		NipponCandle nipponCandle = nipponCandles.get(86);
		assertEquals("2024-09-30", nipponCandle.getDateString());
		assertEquals("21:45:00", nipponCandle.getTimeString());
		assertEquals(new BigDecimal("0.00695341"), nipponCandle.getOpen());
		assertEquals(new BigDecimal("0.00695545"), nipponCandle.getHigh());
		assertEquals(new BigDecimal("0.00694886"), nipponCandle.getLow());
		assertEquals(new BigDecimal("0.00695523"), nipponCandle.getClose());
	}

	@Test
	void shouldRetainSameValuesForNipponCandle_94() {
		NipponCandle nipponCandle = nipponCandles.get(94);
		assertEquals("2024-09-30", nipponCandle.getDateString());
		assertEquals("23:45:00", nipponCandle.getTimeString());
		assertEquals(new BigDecimal("0.00696409"), nipponCandle.getOpen());
		assertEquals(new BigDecimal("0.00696455"), nipponCandle.getHigh());
		assertEquals(new BigDecimal("0.00696318"), nipponCandle.getLow());
		assertEquals(new BigDecimal("0.00696364"), nipponCandle.getClose());
	}

	@Test
	void shouldRetainSameValuesForNipponCandleForPreviousDayClose() {
		NipponCandle nipponCandle = nipponCandlesConversionService.convertPreviousDayClose(rawDataContainer);
		assertEquals("2024-09-27", nipponCandle.getDateString());
		assertEquals("23:59:00", nipponCandle.getTimeString());
		assertEquals(new BigDecimal("0.00703318"), nipponCandle.getOpen());
		assertEquals(new BigDecimal("0.00703318"), nipponCandle.getHigh());
		assertEquals(new BigDecimal("0.00703318"), nipponCandle.getLow());
		assertEquals(new BigDecimal("0.00703318"), nipponCandle.getClose());
	}

	/*
	@Test
	void shouldRetainSameValuesForNipponCandle_X() {
		NipponCandle nipponCandle = nipponCandles.get();
		assertEquals("2024-09-30", nipponCandle.getDateString());
		assertEquals("", nipponCandle.getTimeString());
		assertEquals(new BigDecimal(""), nipponCandle.getOpen());
		assertEquals(new BigDecimal(""), nipponCandle.getHigh());
		assertEquals(new BigDecimal(""), nipponCandle.getLow());
		assertEquals(new BigDecimal(""), nipponCandle.getClose());
	}*/
}