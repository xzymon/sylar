package com.xzymon.sylar.consumer;

import com.xzymon.sylar.constants.IntervalHelper;
import com.xzymon.sylar.constants.MonthPlMapping;
import com.xzymon.sylar.constants.ValorNameHelper;
import com.xzymon.sylar.constants.marker.MarkerCharacter;
import com.xzymon.sylar.helper.ValuesAreaColors;
import com.xzymon.sylar.model.*;
import com.xzymon.sylar.predicate.*;
import com.xzymon.sylar.helper.PathsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class CmcProcessFilesConsumer implements ProcessFilesConsumer {
	private static final int CHECK_MARGIN = 3;
	private static final int FRAME_LINE_WIDTH = 1;
	private static final int HGAUGE_SAFE_DISTANCE = 30;

	@Value("${default.year}")
	private Integer defaultYearForNewFilename;

	@Value("${processing.files.rename}")
	private boolean renameFiles;


	@Override
	public void accept(PathsDto pathsDto) {
		log.info(String.format("Processing file: %1$s", pathsDto.getPathToInputFile().getFileName().toString()));
		//CsvOutput csvOutput = processSingleFileForPath(pathsDto.getPathToInputFile());
		CmcRawDataContainer rawDataContainer = new CmcRawDataContainer();
		try {
			storeInPngFile(rawDataContainer, pathsDto.getPathToInputFile(), pathsDto.getGeneratedPngDirectory());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		log.info(String.format("File %1$s processed.", pathsDto.getPathToInputFile().getFileName().toString()));
		moveFile(pathsDto.getPathToInputFile(), pathsDto.getLoadingDirectoryProcessed(), rawDataContainer.getPngFileNewName());
		log.info(String.format("File %1$s moved to processed directory.", pathsDto.getPathToInputFile().getFileName().toString()));
		//storeInCsvFile(pathsDto.getGeneratedCsvDirectory(), csvOutput);
	}

    /*
    private CsvOutput processSingleFileForPath(Path path) throws IOException {
        PngImage png = PngImage.read(new File(path.toString()));
        BufferedPaletteImage buffPalImg = (BufferedPaletteImage) ImageDecoder.toImage(png);
        StqRawDataContainer container = stockImageProcessingService.extractRawDataFromImage(buffPalImg);
        return stockImageProcessingService.toCsvOutput(container);
    }*/

	private CmcRawDataContainer storeInPngFile(CmcRawDataContainer rawDataContainer, Path inputPath, String genPngDir) throws IOException {
		log.info("Processing file: " + inputPath.toString());
		BufferedImage image = ImageIO.read(new File(inputPath.toString()));

		rawDataContainer.setSourceFileName(inputPath.getFileName().toString());

		FrameCoords snapshotDateTimeFC = new FrameCoords( 9, 300, 37, 33);
		extractSnapshotDateTimeAreaInDataContainer(rawDataContainer, snapshotDateTimeFC, image);

		FrameCoords notChartMarkerFC = new FrameCoords( 77, 90, 120, 35);
		determineIfIsNotChartMarker(rawDataContainer, notChartMarkerFC, image);

		// dalsze pobieranie danych nie ma sensu jeżeli to jednak nie jest prawidłowy obraz z wykresem
		if (!rawDataContainer.isNotChart()) {
			FrameCoords valorNameFC = new FrameCoords(165, 182, 201, 30);
			extractValorNameAreaInDataContainer(rawDataContainer, valorNameFC, image);

			FrameCoords narrowToolboxMarkerFC = new FrameCoords( 1910, 119, 1940, 89);
			determineIfIsToolboxMarker(rawDataContainer, narrowToolboxMarkerFC, image);

			// toolbox flag może się pojawić w różnym miejscu - w zależności który widok jest na zdjęciu
			// najpierw (powyżej) przeprowadzono próbę sprawdzenia czy toolboxMarker jest obecny w położeniu charakterystycznym dla wąskiego wykresu
			// więc poniżej jest sprawdzenie czy flaga została ustawiona -> jeżeli tak to to jest wąski wykres
			if (rawDataContainer.isToolboxFlag()) {
				rawDataContainer.setNarrowFlag(true);
			} else {
				// skoro flaga nie została ustawiona - to teraz bieżemy ustawienia charakterystyczne dla szerokiego wykresu
				FrameCoords wideToolboxMarkerFC = new FrameCoords(1910, 163, 1940, 133);
				// i sprawdzamy czy zostanie wykryty toolboxMarker
				determineIfIsToolboxMarker(rawDataContainer, wideToolboxMarkerFC, image);
				// jeżeli został wykryty toolboxMarker to ustawiamy flagę szerokiego wykresu
				rawDataContainer.setWideFlag(rawDataContainer.isToolboxFlag());
			}

			if (rawDataContainer.isToolboxFlag()) {
				//jeżeli toolboxFlag jest ustawiony, to mamy do czynienia z napisem w 2 rzędach - więc próbujemy zmapować znaki z 2 rzędów
				FrameCoords intervalLine1FC = new FrameCoords(1941, 101, 1963, 66);
				extractIntervalLine1AreaInDataContainer(rawDataContainer, intervalLine1FC, image);

				FrameCoords intervalLine2FC = new FrameCoords(1974, 113, 1991, 66);
				extractIntervalLine2AreaInDataContainer(rawDataContainer, intervalLine2FC, image);
			} else {
				// jeżeli nie jest ustawiony toolboxFlag - to znaczy że widok zawiera napis "Opcje" i interwał jest zapisany w jednym wierszu
				// - zatem należy zmapować kod interwału dla znanego charakterystycznego położenia tego kodu
				FrameCoords intervalOptionsFC = new FrameCoords(1906, 81, 1926, 22);
				extractIntervalOptionsAreaInDataContainer(rawDataContainer, intervalOptionsFC, image);
				//ale w tym wypadku nie wiadomo czy wykres jest wąski czy szeroki
			}


			if (renameFiles) {
				preparePngNewName(rawDataContainer);
			}

        /*
        FrameCoords valuesFC = new FrameCoords( 381, 1852, 1897, 0);
        int[] valuesPixels = extractFrameAsImage("values", valuesFC, genPngDir, image);

        FrameCoords valuesAloneFC = new FrameCoords( 413, 1740, 1890, 10);
        int[] valuesAlonePixels = extractFrameAsImage("valuesAlone", valuesAloneFC, genPngDir, image);

        FrameCoords verticalGaugesFC = new FrameCoords( 381, 1852, 406, 0);
        int[] verticalGaugesPixels = extractFrameAsImage("verticalGauges", verticalGaugesFC, genPngDir, image);

        FrameCoords horizontalGaugesFC = new FrameCoords( 414, 1852, 1890, 1739);
        int[] horizontalGaugesPixels = extractFrameAsImage("horizontalGauges", horizontalGaugesFC, genPngDir, image);
         */
		} else {
			if (renameFiles) {
				String ordinalNumber = rawDataContainer.getSourceFileName().substring(3, 8);
				String proposedNewName = String.format("nc_%1$s%2$s.png", rawDataContainer.getDateTimePartForNewFileName(), ordinalNumber);
				rawDataContainer.setPngFileNewName(proposedNewName);
			}
		}

		if (!rawDataContainer.isNotChart()) {
			if (rawDataContainer.isToolboxFlag()) {
				if (rawDataContainer.isNarrowFlag()) {
					FrameCoords defaultNarrowToolboxValuesFrame = new FrameCoords(414, 1738, 1889, 10);
					determineValuesFrameCoordsByMatchingCorners(rawDataContainer, defaultNarrowToolboxValuesFrame, image, CHECK_MARGIN);
				}
				if (rawDataContainer.isWideFlag()) {
					FrameCoords defaultWideToolboxValuesFrame = new FrameCoords(414, 2618, 1889, 10);
					determineValuesFrameCoordsByMatchingCorners(rawDataContainer, defaultWideToolboxValuesFrame, image, CHECK_MARGIN);
				}
			} else {
				// po prostu testujemy czy któreś wpadnie - a jak wpadnie to ustawianie odpowiedniej flagi
				FrameCoords defaultNarrowValuesFrame = new FrameCoords(414, 1738, 1989, 10);
				determineValuesFrameCoordsByMatchingCorners(rawDataContainer, defaultNarrowValuesFrame, image, CHECK_MARGIN);
				if (rawDataContainer.isValuesFrameFlag()) {
					log.info("setting narrow flag");
					rawDataContainer.setNarrowFlag(true);
				} else {
					FrameCoords defaultWideValuesFrame = new FrameCoords(414, 2618, 1989, 10);
					determineValuesFrameCoordsByMatchingCorners(rawDataContainer, defaultWideValuesFrame, image, CHECK_MARGIN);
					if (rawDataContainer.isValuesFrameFlag()) {
						log.info("setting wide flag");
						rawDataContainer.setWideFlag(true);
					}
				}
			}

			if (rawDataContainer.isValuesFrameFlag()) {
				log.info("values frame detected");
				// wykrywanie napisów na osiach
				// oś X - konkretne texty odnoszą się do prowadnic pionowych
				extractVerticalGaugesLocations(rawDataContainer, image);
				// oś Y - konkretne texty odnoszą się do prowadnic poziomych
				extractHorizontalGaugesLocations(rawDataContainer, image);
				// tworzenie surowych świec z wykresu
				extractRawCandles(rawDataContainer, image);
				// mapowanie położenia na osi X na czas
				// mapowanie połozenia na osi Y na wartość
			}
		}

		return rawDataContainer;
	}

	private void preparePngNewName(CmcRawDataContainer rawDataContainer) {
		String datePart = rawDataContainer.getDateTimePartForNewFileName();
		String rawValorNameText = rawDataContainer.getValorNameArea().getExtractedText();

		if (rawValorNameText != null) {
			String mappedValorNameText = ValorNameHelper.VALOR_NAME_MAP.get(rawValorNameText);
			if (mappedValorNameText == null) {
				throw new RuntimeException("Unknown valor name text: " + rawValorNameText);
			}

			if (rawDataContainer.isToolboxFlag()) {
				String rawIntevalLine1 = rawDataContainer.getIntervalLine1Area().getExtractedText();
				String rawIntevalLine2 = rawDataContainer.getIntervalLine2Area().getExtractedText();

				if (rawIntevalLine1 != null && rawIntevalLine2 != null) {
					String mappedIntervalLine1 = IntervalHelper.LENGTHS_MAP.get(rawIntevalLine1);
					if (mappedIntervalLine1 == null) {
						throw new RuntimeException("Unknown interval line 1 text: " + rawIntevalLine1);
					}
					String mappedIntervalLine2 = IntervalHelper.UNITS_MAP.get(rawIntevalLine2);
					if (mappedIntervalLine2 == null) {
						throw new RuntimeException("Unknown interval line 2 text: " + rawIntevalLine2);
					}
					String extractedOrdinalNumber = rawDataContainer.getSourceFileName().substring(3, 8);
					String newValue = mappedValorNameText + "_b" + mappedIntervalLine1 + mappedIntervalLine2 + "_" + datePart + extractedOrdinalNumber + ".png";
					rawDataContainer.setPngFileNewName(newValue);
				}
			} else {
				String rawIntervalOptions = rawDataContainer.getIntervalOptionsArea().getExtractedText();

				if (rawIntervalOptions != null) {
					String mappedIntervalOptions = IntervalHelper.LENGTH_WITH_UNIT_MAP.get(rawIntervalOptions);
					if (mappedIntervalOptions == null) {
						throw new RuntimeException("Unknown interval options text: " + rawIntervalOptions);
					}
					String extractedOrdinalNumber = rawDataContainer.getSourceFileName().substring(3, 8);
					String newValue = mappedValorNameText + "_b" + mappedIntervalOptions + "_" + datePart + extractedOrdinalNumber + ".png";
					rawDataContainer.setPngFileNewName(newValue);
				}
			}
		}
	}

	private void extractSnapshotDateTimeAreaInDataContainer(CmcRawDataContainer rawDataContainer, FrameCoords frameCoords, BufferedImage image) throws IOException {
		rawDataContainer.setSnapshotDateTimeFC(frameCoords);
		TextPixelFlattenedArea snapshotDateTimeArea = rawDataContainer.getSnapshotDateTimeArea();
		//snapshotDateTimeArea.setPixelArea(extractFrameAsImage("snapshotDateTime", rawDataContainer.getSnapshotDateTimeFC(), genPngDir, image));
		snapshotDateTimeArea.setPixelArea(extractPixelsFromFrame(rawDataContainer.getSnapshotDateTimeFC(), image));
		snapshotDateTimeArea.setXLength(frameCoords.getRight() - frameCoords.getLeft());
		snapshotDateTimeArea.setYLength(frameCoords.getBottom() - frameCoords.getTop());
		int[] scannedVertically = scanVerticallyRemappingToMonochromaticComparingToTopLeftPixel(snapshotDateTimeArea);
		String detectedText = detectTrimmedCharsAndRun(scannedVertically, snapshotDateTimeArea, Map.of(), new FontSize17ReportingUnknownTrimmedShapePredicate());
		snapshotDateTimeArea.setExtractedText(detectedText);
		String[] splitted = detectedText.split(" ");
		if (splitted.length == 4) {
			LocalTime localTime = LocalTime.parse(splitted[0], DateTimeFormatter.ofPattern("HH:mm"));
			Integer day = Integer.parseInt(splitted[2]);
			Integer month = MonthPlMapping.MAC_MMM_TO_INT.get(splitted[3]);
			LocalDateTime snapshotDateTime = LocalDateTime.of(defaultYearForNewFilename, month, day, localTime.getHour(), localTime.getMinute());
			rawDataContainer.setDateTimePartForNewFileName(DateTimeFormatter.ofPattern("yyyyMMdd").format(snapshotDateTime) + "T" + DateTimeFormatter.ofPattern("HHmm").format(snapshotDateTime));
		}
	}

	private static void determineIfIsNotChartMarker(CmcRawDataContainer rawDataContainer, FrameCoords frameCoords, BufferedImage image) throws IOException {
		rawDataContainer.setNotChartMarkerFC(frameCoords);
		TextPixelFlattenedArea notChartMarkerArea = rawDataContainer.getNotChartMarkerArea();
		notChartMarkerArea.setPixelArea(extractPixelsFromFrame(rawDataContainer.getNotChartMarkerFC(), image));
		notChartMarkerArea.setXLength(frameCoords.getRight() - frameCoords.getLeft());
		notChartMarkerArea.setYLength(frameCoords.getBottom() - frameCoords.getTop());
		int[] scannedVertically = scanVerticallyRemappingToMonochromaticComparingToTopLeftPixel(notChartMarkerArea);
		try {
			String detectedText = detectTrimmedCharsAndRun(scannedVertically, notChartMarkerArea, Map.of(), new MarkerReportingUnknownTrimmedShapePredicate());
			rawDataContainer.setNotChart(detectedText.equals(MarkerCharacter.NOT_CHART));
		} catch (RuntimeException e) {
			rawDataContainer.setNotChart(false);
		}
	}

	private static void determineIfIsToolboxMarker(CmcRawDataContainer rawDataContainer, FrameCoords frameCoords, BufferedImage image) throws IOException {
		rawDataContainer.setToolboxMarkerFC(frameCoords);
		TextPixelFlattenedArea toolboxMarkerArea = rawDataContainer.getToolboxMarkerArea();
		int[] pixelArea = extractPixelsFromFrame(rawDataContainer.getToolboxMarkerFC(), image);
		toolboxMarkerArea.setPixelArea(recolorAllWithMap(getFF121212RecoloringMap(), pixelArea));
		toolboxMarkerArea.setXLength(frameCoords.getRight() - frameCoords.getLeft());
		toolboxMarkerArea.setYLength(frameCoords.getBottom() - frameCoords.getTop());
		int[] scannedVertically = scanVerticallyRemappingToMonochromaticComparingToTopLeftPixel(toolboxMarkerArea);
		try {
			String detectedText = detectTrimmedCharsAndRun(scannedVertically, toolboxMarkerArea, Map.of(), new MarkerReportingUnknownTrimmedShapePredicate());
			rawDataContainer.setToolboxFlag(detectedText.equals(MarkerCharacter.TOOLBOX));
		} catch (RuntimeException e) {
			rawDataContainer.setToolboxFlag(false);
		}
	}

	private static void extractValorNameAreaInDataContainer(CmcRawDataContainer rawDataContainer, FrameCoords frameCoords, BufferedImage image) throws IOException {
		rawDataContainer.setValorNameFC(frameCoords);
		TextPixelFlattenedArea valorNameArea = rawDataContainer.getValorNameArea();
		valorNameArea.setPixelArea(extractPixelsFromFrame(rawDataContainer.getValorNameFC(), image));
		valorNameArea.setXLength(frameCoords.getRight() - frameCoords.getLeft());
		valorNameArea.setYLength(frameCoords.getBottom() - frameCoords.getTop());
		int[] scannedVertically = scanVerticallyRemappingToMonochromaticComparingToTopLeftPixel(valorNameArea);
		String detectedText = detectTrimmedCharsAndRun(scannedVertically, valorNameArea, Map.of(), new FontSize27ReportingUnknownTrimmedShapePredicate());
		String trimmedText = detectedText.trim();
		valorNameArea.setExtractedText(trimmedText);
	}

	private static void extractIntervalLine1AreaInDataContainer(CmcRawDataContainer rawDataContainer, FrameCoords frameCoords, BufferedImage image) throws IOException {
		rawDataContainer.setIntervalLine1FC(frameCoords);
		TextPixelFlattenedArea intervalLine1Area = rawDataContainer.getIntervalLine1Area();
		int[] pixelArea = extractPixelsFromFrame(rawDataContainer.getIntervalLine1FC(), image);
		intervalLine1Area.setPixelArea(recolorAllWithMap(getFF121212RecoloringMap(), pixelArea));
		intervalLine1Area.setXLength(frameCoords.getRight() - frameCoords.getLeft());
		intervalLine1Area.setYLength(frameCoords.getBottom() - frameCoords.getTop());
		int[] scannedVertically = scanVerticallyRemappingToMonochromaticComparingToTopLeftPixel(intervalLine1Area);
		String detectedText = detectTrimmedCharsAndRun(scannedVertically, intervalLine1Area, Map.of(), new FontSize22ReportingUnknownTrimmedShapePredicate());
		String trimmedText = detectedText.trim();
		intervalLine1Area.setExtractedText(trimmedText);
	}

	private static void extractIntervalLine2AreaInDataContainer(CmcRawDataContainer rawDataContainer, FrameCoords frameCoords, BufferedImage image) throws IOException {
		rawDataContainer.setIntervalLine2FC(frameCoords);
		TextPixelFlattenedArea intervalLine2Area = rawDataContainer.getIntervalLine2Area();
		int[] pixelArea = extractPixelsFromFrame(rawDataContainer.getIntervalLine2FC(), image);
		intervalLine2Area.setPixelArea(recolorAllWithMap(getFF121212RecoloringMap(), pixelArea));
		intervalLine2Area.setXLength(frameCoords.getRight() - frameCoords.getLeft());
		intervalLine2Area.setYLength(frameCoords.getBottom() - frameCoords.getTop());
		int[] scannedVertically = scanVerticallyRemappingToMonochromaticComparingToTopLeftPixel(intervalLine2Area);
		String detectedText = detectTrimmedCharsAndRun(scannedVertically, intervalLine2Area, Map.of(), new FontSize16ReportingUnknownTrimmedShapePredicate());
		String trimmedText = detectedText.trim();
		intervalLine2Area.setExtractedText(trimmedText);
	}

	private static void extractIntervalOptionsAreaInDataContainer(CmcRawDataContainer rawDataContainer, FrameCoords frameCoords, BufferedImage image) throws IOException {
		rawDataContainer.setIntervalOptionsFC(frameCoords);
		TextPixelFlattenedArea intervalOptionsArea = rawDataContainer.getIntervalOptionsArea();
		intervalOptionsArea.setPixelArea(extractPixelsFromFrame(rawDataContainer.getIntervalOptionsFC(), image));
		intervalOptionsArea.setXLength(frameCoords.getRight() - frameCoords.getLeft());
		intervalOptionsArea.setYLength(frameCoords.getBottom() - frameCoords.getTop());
		int[] scannedVertically = scanVerticallyRemappingToMonochromaticComparingToTopLeftPixel(intervalOptionsArea);
		Map<Integer, Integer> colorsReplacementMap = Map.of(
				-13224394, -16777216,   // -13224394 = 0xff363636 ; #363636 -> #000
				-10790053, -16777216,      // -10790053 = 0xff5b5b5b ; #5b5b5b -> #000
				-3487030, -16777216         //  -16777216 = 0xffcacaca ;  #cacaca -> #000
		);
		String detectedText = detectTrimmedCharsAndRun(scannedVertically, intervalOptionsArea, colorsReplacementMap, new FontSize20ReportingUnknownTrimmedShapePredicate());
		String trimmedText = detectedText.trim();
		intervalOptionsArea.setExtractedText(trimmedText);
	}

	private void determineValuesFrameCoordsByMatchingCorners(CmcRawDataContainer rawDataContainer, FrameCoords defaultValuesFrame, BufferedImage image, int margin) throws IOException {
		FrameCoords topLeftFC = getFrameCoordsWithMarginForXY(defaultValuesFrame.getLeft(), defaultValuesFrame.getTop(), margin);
		boolean topLeftCornerFlag = checkCornerExistence(topLeftFC, image, MarkerCharacter.CORNER_TOP_LEFT_7);

		FrameCoords topRightFC = getFrameCoordsWithMarginForXY(defaultValuesFrame.getRight(), defaultValuesFrame.getTop(), margin);
		boolean topRightCornerFlag = checkCornerExistence(topRightFC, image, MarkerCharacter.CORNER_TOP_RIGHT_7);

		FrameCoords bottomRightFC = getFrameCoordsWithMarginForXY(defaultValuesFrame.getRight(), defaultValuesFrame.getBottom(), margin);
		boolean bottomRightCornerFlag = checkCornerExistence(bottomRightFC, image, MarkerCharacter.CORNER_BOTTOM_RIGHT_7);

		FrameCoords bottomLeftFC = getFrameCoordsWithMarginForXY(defaultValuesFrame.getLeft(), defaultValuesFrame.getBottom(), margin);
		boolean bottomLeftCornerFlag = checkCornerExistence(bottomLeftFC, image, MarkerCharacter.CORNER_BOTTOM_LEFT_7);

		if (topLeftCornerFlag && topRightCornerFlag && bottomRightCornerFlag && bottomLeftCornerFlag) {
			rawDataContainer.setValuesFrame(defaultValuesFrame);
			rawDataContainer.setValuesFrameFlag(true);
		} else {
			throw new RuntimeException("values frame not detected");
		}
	}

	private boolean checkCornerExistence(FrameCoords frameCoords, BufferedImage image, String expectedTextToDetect) throws IOException {
		log.info("checking corner existence - expecting text: " + expectedTextToDetect);
		TextPixelFlattenedArea valuesFrameTopLeftArea = new TextPixelFlattenedArea();
		valuesFrameTopLeftArea.setPixelArea(extractPixelsFromFrame(frameCoords, image));
		valuesFrameTopLeftArea.setXLength(frameCoords.getRight() - frameCoords.getLeft());
		valuesFrameTopLeftArea.setYLength(frameCoords.getBottom() - frameCoords.getTop());
		int[] scannedVertically = scanVerticallyRemappingToMonochromaticComparingToTopLeftPixel(valuesFrameTopLeftArea);
		String detectedText;
		try {
			detectedText = detectCharAndRun(scannedVertically, valuesFrameTopLeftArea, Map.of(), new MarkerReportingUnknownTrimmedShapePredicate());
			log.info("detected: " + detectedText);
		} catch (RuntimeException e) {
			return false;
		}
		return detectedText.equals(expectedTextToDetect);
	}

	private FrameCoords getFrameCoordsWithMarginForXY(int x, int y, int margin) {
		return new FrameCoords(y - margin, x + margin + 1, y + margin + 1, x - margin);
	}

	private void extractVerticalGaugesLocations(CmcRawDataContainer rawDataContainer, BufferedImage image) throws IOException {
		List<Integer> vGauges = detectReferencePointsForVerticalGauges(image, rawDataContainer.getValuesFrame());
		if (!vGauges.isEmpty()) {
			log.info("Detected vertical gauges: {{}}", vGauges.size());
			for (int i = 0; i < vGauges.size(); i++) {
				log.info("[{}] = {}", i, vGauges.get(i));
			}
		}
	}

	private List<Integer> detectReferencePointsForVerticalGauges(BufferedImage image, FrameCoords valuesFrame) throws IOException {
		FrameCoords frameCoords = new FrameCoords(valuesFrame.getTop()-CHECK_MARGIN, valuesFrame.getRight(), valuesFrame.getTop(), valuesFrame.getLeft());
		TextPixelFlattenedArea referencePointsFrameArea = new TextPixelFlattenedArea();
		referencePointsFrameArea.setPixelArea(extractPixelsFromFrame(frameCoords, image));
		referencePointsFrameArea.setXLength(frameCoords.getRight() - frameCoords.getLeft());
		referencePointsFrameArea.setYLength(frameCoords.getBottom() - frameCoords.getTop());
		int[] scannedVertically = scanVerticallyRemappingToMonochromaticComparingToTopLeftPixel(referencePointsFrameArea);
		// można po prostu brać wartości z scannedVertically - tam gdzie jest 3 - tam jest referencePoint
		// tylko trzeba znormalizować położenie (do rozważenia)
		List<Integer> gauges = new ArrayList<>();
		for (int i = 0; i < scannedVertically.length; i++) {
			if (scannedVertically[i] == CHECK_MARGIN) {
				gauges.add(i);
			}
		}
		return gauges;
	}

	private void extractHorizontalGaugesLocations(CmcRawDataContainer rawDataContainer, BufferedImage image) throws IOException {
		List<Integer> hGauges = detectReferencePointsForHorizontalGauges(image, rawDataContainer.getValuesFrame());
		List<Integer> hGaugesWithoutCurrentLevel = detectReferencePointsForHorizontalGaugesWithoutCurrentLevel(image, rawDataContainer.getValuesFrame());
		List<Integer> currentLevel = hGauges.stream()
				.filter(gauge -> !hGaugesWithoutCurrentLevel.contains(gauge))
				.collect(Collectors.toList());
		// log levels
		if (!hGaugesWithoutCurrentLevel.isEmpty()) {
			log.info("Detected horizontal gauges: {{}}", hGaugesWithoutCurrentLevel.size());
			for (int i = 0; i < hGaugesWithoutCurrentLevel.size(); i++) {
				log.info("[{}] = {}", i, hGaugesWithoutCurrentLevel.get(i));
			}
		}
		if (!currentLevel.isEmpty()) {
			log.info("Detected current level gauges: {{}}", currentLevel.size());
			for (int i = 0; i < currentLevel.size(); i++) {
				log.info("[{}] = {}", i, currentLevel.get(i));
			}
		}

		// z kilku powodów tekst odnoszący się do prowadnicy może sprawiać problemy
		// w takich sytuacjach najlepiej jest po prostu usunąc tą prowadnicę z listy
		// (i tak ta wartość zostanie potem odtworzona przez interpolację z prowadnic nie sprawiających problemów)
		List<Integer> hGaugesSafeToProcess = new ArrayList<>();
		hGaugesSafeToProcess.addAll(hGaugesWithoutCurrentLevel);
		// tekst odnoszący się do currentLevel może zakrywać najbliższe prowadnice i ich texty
		// - pozbywamy się tych "zagrożonych" textów
		for (int i = 0; i < currentLevel.size(); i++) {
			Integer checkLevel = currentLevel.get(i);
			for (int j = 0; j < hGaugesSafeToProcess.size(); j++) {
				Integer hGauge = hGaugesSafeToProcess.get(j);
				if (Math.abs(checkLevel - hGauge) < HGAUGE_SAFE_DISTANCE) {
					hGaugesSafeToProcess.remove(j);
					j--;
				}
			}
		}
		// usuwanie ostatniej prowadnicy (tej najniższej) - bo jest ryzyko że jest zbyt blisko dolnej krawędzi wykresu
		// - i może sprawiać problemy
		hGaugesSafeToProcess.remove(hGaugesSafeToProcess.size()-1);

		log.info("Safe gauges: {{}}", currentLevel.size());
		for (int i = 0; i < hGaugesSafeToProcess.size(); i++) {
			log.info("[{}] = {}", i, hGaugesSafeToProcess.get(i));
		}

		log.info("Flags: toolbox = {}, narrow = {}, wide = {}", rawDataContainer.isToolboxFlag(), rawDataContainer.isNarrowFlag(), rawDataContainer.isWideFlag());

		Integer referencePoint;
		FrameCoords remappedCoordsForText;
		for (int i = 0; i < hGaugesSafeToProcess.size(); i++) {
			referencePoint = hGaugesSafeToProcess.get(i);
			remappedCoordsForText = getHorizontalGaugeTextCoords(referencePoint, rawDataContainer);
			extractHorizontalGaugeAreaInDataContainer(referencePoint, rawDataContainer, remappedCoordsForText, image);
		}

		TextPixelFlattenedArea pixelArea;
		BigDecimal valueForReferencePoint;
		for (int i = 0; i < hGaugesSafeToProcess.size(); i++) {
			referencePoint = hGaugesSafeToProcess.get(i);
			pixelArea = rawDataContainer.getHorizontalGauges().get(referencePoint);
			valueForReferencePoint = new BigDecimal(pixelArea.getExtractedText().replace(",", "."));
			pixelArea.setParsedBDValue(valueForReferencePoint);
			log.info("[{}]: referencePoint = {}, extractedText = {}, parsedBDValue = {}", i, referencePoint, pixelArea.getExtractedText(), pixelArea.getParsedBDValue());
		}

		if (hGaugesSafeToProcess.size() < 2) {
			throw new RuntimeException("Not enough gauges to interpolate");
		}

		extrapolateHorizontalValuesToTop(hGaugesSafeToProcess, rawDataContainer);
		interpolateHorizontalValues(hGaugesSafeToProcess, rawDataContainer);
		extrapolateHorizontalValuesToBottom(hGaugesSafeToProcess, rawDataContainer);
	}

	private void extrapolateHorizontalValuesToTop(List<Integer> hGaugesSafeToProcess, CmcRawDataContainer rawDataContainer) {
		log.info("Extrapolating horizontal gauges : [0] -> top");

		Integer safeRefPoint0 = hGaugesSafeToProcess.get(0);
		Integer safeRefPoint1 = hGaugesSafeToProcess.get(1);

		BigDecimal refPoint0Value = rawDataContainer.getHorizontalGauges().get(safeRefPoint0).getParsedBDValue();
		BigDecimal refPoint1Value = rawDataContainer.getHorizontalGauges().get(safeRefPoint1).getParsedBDValue();

		// w mianowniku ułamka odejmowane są położenia pikseli: safeRefPoint1 - safeRefPoint0
		// bo indeksy narastają odwrotnie do kierunku w jakim narastają wartości
		// (położenia(=indeksy) rosną "w dół", wartości rosną "w górę" na wykresie)
		// tu potrzebna jest jedynie wartość różnicy - żeby była dodatnia trzeba odejmować w tym kierunku (no bo musi być większe minus mniejsze)
		// można by też robić absolute - ale po co dokładać dodatkową operację ?
		BigDecimal perPixel = refPoint0Value.subtract(refPoint1Value).divide(new BigDecimal(safeRefPoint1 - safeRefPoint0), 5, RoundingMode.HALF_UP);

		Map<Integer, BigDecimal> hvMap = rawDataContainer.getHorizontalValuesMap();

		BigDecimal extrapolatedValue, scaledValue;
		Integer index;
		for (int i = 1; i < safeRefPoint0; i++) {
			index = safeRefPoint0 - i;
			extrapolatedValue = refPoint0Value.add(perPixel.multiply(new BigDecimal(i)));
			scaledValue = extrapolatedValue.setScale(3, RoundingMode.HALF_UP);
			hvMap.put(index, scaledValue);
			log.info("Extrapolated value: [{}]: {}", index, hvMap.get(index));
		}
	}

	private void interpolateHorizontalValues(List<Integer> hGaugesSafeToProcess, CmcRawDataContainer rawDataContainer) {
		log.info("Interpolating horizontal gauges : [{}] -> [{}]");
	}

	private void extrapolateHorizontalValuesToBottom(List<Integer> hGaugesSafeToProcess, CmcRawDataContainer rawDataContainer) {
		log.info("Extrapolating horizontal gauges : [last] -> bottom");

		Integer safeRefPointBeforeLast = hGaugesSafeToProcess.get(hGaugesSafeToProcess.size()-2);
		Integer safeRefPointLast = hGaugesSafeToProcess.get(hGaugesSafeToProcess.size()-1);

		BigDecimal refPointBeforeLastValue = rawDataContainer.getHorizontalGauges().get(safeRefPointBeforeLast).getParsedBDValue();
		BigDecimal refPointLastValue = rawDataContainer.getHorizontalGauges().get(safeRefPointLast).getParsedBDValue();

		// w mianowniku ułamka odejmowane są położenia pikseli: safeRefPointLast - safeRefPointBeforeLast
		// bo indeksy narastają odwrotnie do kierunku w jakim narastają wartości
		// (położenia(=indeksy) rosną "w dół", wartości rosną "w górę" na wykresie)
		// tu potrzebna jest jedynie wartość różnicy - żeby była dodatnia trzeba odejmować w tym kierunku (no bo musi być większe minus mniejsze)
		// można by też robić absolute - ale po co dokładać dodatkową operację ?
		BigDecimal perPixel = refPointBeforeLastValue.subtract(refPointLastValue).divide(new BigDecimal(safeRefPointLast - safeRefPointBeforeLast), 5, RoundingMode.HALF_UP);

		Map<Integer, BigDecimal> hvMap = rawDataContainer.getHorizontalValuesMap();

		BigDecimal extrapolatedValue, scaledValue;
		Integer index;
		Integer bottom = rawDataContainer.getValuesFrame().getBottom(); // dolna krawędź wykresu

		// idziemy od następnego po ostatnim punkcie o znanej wartości -> w dół
		// zatem indeksy rosną, przemnożoną różnicę na piksel odejmujemy od wartości ostatniego znanego punktu
		for (int i = safeRefPointLast + 1; i < bottom; i++) {
			index = i - safeRefPointLast;
			extrapolatedValue = refPointLastValue.subtract(perPixel.multiply(new BigDecimal(index)));
			scaledValue = extrapolatedValue.setScale(3, RoundingMode.HALF_UP);
			hvMap.put(i, scaledValue);
			log.info("Extrapolated value: [{}]: {}", i, hvMap.get(i));
		}
	}

	private FrameCoords getHorizontalGaugeTextCoords(Integer referencePoint, CmcRawDataContainer rawDataContainer) {
		log.info("referencePoint: {}", referencePoint);
		FrameCoords toolboxNarrowRefPointToTextAreaOffset = new FrameCoords(405, 1852, 427, 1742);

		FrameCoords modifiersFC = null;
		if (rawDataContainer.isToolboxFlag() && rawDataContainer.isNarrowFlag()) {
			modifiersFC = toolboxNarrowRefPointToTextAreaOffset;
		}

		return new FrameCoords(modifiersFC.getTop() + referencePoint, modifiersFC.getRight(), modifiersFC.getBottom() + referencePoint, modifiersFC.getLeft());
	}

	private List<Integer> detectReferencePointsForHorizontalGauges(BufferedImage image, FrameCoords valuesFrame) throws IOException {

		//FrameCoords frameCoords = new FrameCoords(valuesFrame.getTop(), valuesFrame.getRight()+4, valuesFrame.getBottom(), valuesFrame.getRight()+1);
		FrameCoords frameCoords = new FrameCoords(valuesFrame.getTop(), valuesFrame.getRight()+FRAME_LINE_WIDTH+CHECK_MARGIN, valuesFrame.getBottom(), valuesFrame.getRight()+FRAME_LINE_WIDTH);
		TextPixelFlattenedArea referencePointsFrameArea = new TextPixelFlattenedArea();
		referencePointsFrameArea.setPixelArea(extractPixelsFromFrame(frameCoords, image));
		referencePointsFrameArea.setXLength(frameCoords.getRight() - frameCoords.getLeft());
		referencePointsFrameArea.setYLength(frameCoords.getBottom() - frameCoords.getTop());

		int[] scannedHorizontally = scanHorizontallyRemappingToMonochromaticComparingToTopRightPixel(referencePointsFrameArea);
		// można po prostu brać wartości z scannedHorizontally - tam gdzie jest 3 - tam jest referencePoint
		// tylko trzeba znormalizować położenie (do rozważenia)
		List<Integer> gauges = new ArrayList<>();
		for (int i = 0; i < scannedHorizontally.length; i++) {
			if (scannedHorizontally[i] == CHECK_MARGIN) {
				gauges.add(i);
			}
		}
		return gauges;
	}

	private List<Integer> detectReferencePointsForHorizontalGaugesWithoutCurrentLevel(BufferedImage image, FrameCoords valuesFrame) throws IOException {
		//FrameCoords frameCoords = new FrameCoords(valuesFrame.getTop(), valuesFrame.getRight()+4, valuesFrame.getBottom(), valuesFrame.getRight()+1);
		FrameCoords frameCoords = new FrameCoords(valuesFrame.getTop(), valuesFrame.getRight()+FRAME_LINE_WIDTH+CHECK_MARGIN, valuesFrame.getBottom(), valuesFrame.getRight()+FRAME_LINE_WIDTH);
		TextPixelFlattenedArea referencePointsFrameArea = new TextPixelFlattenedArea();
		int[] pixelArea = extractPixelsFromFrame(frameCoords, image);
		referencePointsFrameArea.setPixelArea(recolorAllWithMap(getCurrentLineRecoloringMap(), pixelArea));
		referencePointsFrameArea.setXLength(frameCoords.getRight() - frameCoords.getLeft());
		referencePointsFrameArea.setYLength(frameCoords.getBottom() - frameCoords.getTop());

		int[] scannedHorizontally = scanHorizontallyRemappingToMonochromaticComparingToTopRightPixel(referencePointsFrameArea);
		// można po prostu brać wartości z scannedHorizontally - tam gdzie jest 3 - tam jest referencePoint
		// tylko trzeba znormalizować położenie (do rozważenia)
		List<Integer> gauges = new ArrayList<>();
		for (int i = 0; i < scannedHorizontally.length; i++) {
			if (scannedHorizontally[i] == CHECK_MARGIN) {
				gauges.add(i);
			}
		}
		return gauges;
	}

	private static void extractHorizontalGaugeAreaInDataContainer(int referencePoint, CmcRawDataContainer rawDataContainer, FrameCoords frameCoords, BufferedImage image) throws IOException {
		TextPixelFlattenedArea hGaugeArea = new TextPixelFlattenedArea();
		int[] pixelArea = extractPixelsFromFrame(frameCoords, image);
		hGaugeArea.setPixelArea(recolorPixelArrayCuttingOffAllBeneathThresholdForChannels(pixelArea, 36, 36, 36, 0, 0, 0));
		hGaugeArea.setXLength(frameCoords.getRight() - frameCoords.getLeft());
		hGaugeArea.setYLength(frameCoords.getBottom() - frameCoords.getTop());
		int[] scannedVertically = scanVerticallyRemappingToMonochromaticComparingToTopLeftPixel(hGaugeArea);
		String detectedText = detectTrimmedCharsAndRun(scannedVertically, hGaugeArea, Map.of(), new FontGaugesReportingUnknownTrimmedShapePredicate());
		String trimmedText = detectedText.trim();
		hGaugeArea.setExtractedText(trimmedText);
		rawDataContainer.getHorizontalGauges().put(referencePoint, hGaugeArea);
	}

	private void extractRawCandles(CmcRawDataContainer rawDataContainer, BufferedImage image) throws IOException {
		FrameCoords frameCoords = rawDataContainer.getValuesFrame();
		PixelFlattenedArea valuesArea = new PixelFlattenedArea();
		valuesArea.setPixelArea(extractPixelsFromFrame(frameCoords, image));
		valuesArea.setXLength(frameCoords.getRight() - frameCoords.getLeft());
		valuesArea.setYLength(frameCoords.getBottom() - frameCoords.getTop());
		Map<Integer, CmcPartialCandle> ascCoreMap = scanVerticallyRemappingToMonochromaticComparingToGivenColor(valuesArea, ValuesAreaColors.ASCENDING_CORE);
		Map<Integer, CmcPartialCandle> ascExtremeMap = scanVerticallyRemappingToMonochromaticComparingToGivenColor(valuesArea, ValuesAreaColors.ASCENDING_EXTREME);
		Map<Integer, CmcPartialCandle> descCoreMap = scanVerticallyRemappingToMonochromaticComparingToGivenColor(valuesArea, ValuesAreaColors.DESCENDING_CORE);
		Map<Integer, CmcPartialCandle> descExtremeMap = scanVerticallyRemappingToMonochromaticComparingToGivenColor(valuesArea, ValuesAreaColors.DESCENDING_EXTREME);
	}

	private static int[] extractFrameAsImage(String fileName, FrameCoords frameCoords, String genPngDir, BufferedImage image) throws IOException {
		LocalDateTime timestampSource = LocalDateTime.now();
		String currentTimestamp = DateTimeFormatter.ofPattern("yyyyMMdd").format(timestampSource) + "T" +  DateTimeFormatter.ofPattern("HHmmss").format(timestampSource);
		BufferedImage subimage = image.getSubimage(frameCoords.getLeft(), frameCoords.getTop(), frameCoords.getRight() - frameCoords.getLeft(), frameCoords.getBottom() - frameCoords.getTop());
		String formatName = "png";
		Path snapshotDateTimePath = Paths.get(genPngDir, fileName + "_" + currentTimestamp + "." + formatName);
		//ImageIO.write(subimage, formatName, new File(snapshotDateTimePath.toString()));
		return subimage.getRGB(0, 0, subimage.getWidth(), subimage.getHeight(), null, 0, subimage.getWidth());
	}

	private static int[] extractPixelsFromFrame(FrameCoords frameCoords, BufferedImage image) throws IOException {
		BufferedImage subimage = image.getSubimage(frameCoords.getLeft(), frameCoords.getTop(), frameCoords.getRight() - frameCoords.getLeft(), frameCoords.getBottom() - frameCoords.getTop());
		return subimage.getRGB(0, 0, subimage.getWidth(), subimage.getHeight(), null, 0, subimage.getWidth());
	}

	private static int[] scanVerticallyRemappingToMonochromaticComparingToTopLeftPixel(TextPixelFlattenedArea tpfArea) {
		return scanVerticallyRemappingToMonochromaticComparingToTopLeftPixel(tpfArea.getPixelArea(), tpfArea.getXLength(), tpfArea.getYLength());
	}

	private static int[] recolorAllWithMap(Map<Integer, Integer> recolorMap, int[] pixelArea) {
		int[] recoloredArray = new int[pixelArea.length];
		System.arraycopy(pixelArea, 0, recoloredArray, 0, pixelArea.length);
		for (Map.Entry<Integer, Integer> entry : recolorMap.entrySet()) {
			int colorToReplace = entry.getKey();
			int colorToReplaceWith = entry.getValue();
			for (int i = 0; i < recoloredArray.length; i++) {
				if (recoloredArray[i] == colorToReplace) {
					recoloredArray[i] = colorToReplaceWith;
				}
			}
		}
		return recoloredArray;
	}

	private static int[] recolorPixelArrayCuttingOffAllBeneathThresholdForChannels(int[] pixelArea, int redChannelThreshold, int greenChannelThreshold, int blueChannelThreshold, int newRed, int newGreen, int newBlue) {
		int[] recoloredArray = new int[pixelArea.length];
		System.arraycopy(pixelArea, 0, recoloredArray, 0, pixelArea.length);
		for (int i = 0; i < recoloredArray.length; i++) {
			int color = recoloredArray[i];
			// red
			int redChannel = (color >> 16) & 0xFF;
			if (redChannel <= redChannelThreshold) {
				redChannel = newRed;
			}
			// green
			int greenChannel = (color >> 8) & 0xFF;
			if (greenChannel <= greenChannelThreshold) {
				greenChannel = newGreen;
			}
			// blue
			int blueChannel = color & 0xFF;
			if (blueChannel <= blueChannelThreshold) {
				blueChannel = newBlue;
			}
			recoloredArray[i] = (redChannel << 16) | (greenChannel << 8) | blueChannel;
		}
		return recoloredArray;
	}

	/**
	 * Scans a 2D pixel array vertically and remaps the data to a monochromatic representation,
	 * comparing each pixel's value to the top-left pixel value. The result is an array where
	 * each element represents the count of differing pixels in each column.
	 *
	 * @param pixelArray an array of pixel values representing a 2D image in row-major order
	 * @param arrWidth the width of the 2D pixel grid
	 * @param arrHeight the height of the 2D pixel grid
	 * @return an integer array representing the count of pixels in each column that differ
	 *         from the top-left pixel value
	 */
	private static int[] scanVerticallyRemappingToMonochromaticComparingToTopLeftPixel(int[] pixelArray, int arrWidth, int arrHeight) {
		int result[] = new int[arrWidth];
		int topLeftColor = pixelArray[0];
		for (int i = 0; i < arrHeight; i++) {
			for (int j = 0; j < arrWidth; j++) {
				if (pixelArray[i*arrWidth + j] != topLeftColor) {
					result[j]++;
				}
			}
		}
		return result;
	}

	private static int[] scanHorizontallyRemappingToMonochromaticComparingToTopRightPixel(TextPixelFlattenedArea tpfArea) {
		return scanHorizontallyRemappingToMonochromaticComparingToTopRightPixel(tpfArea.getPixelArea(), tpfArea.getXLength(), tpfArea.getYLength());
	}

	private static int[] scanHorizontallyRemappingToMonochromaticComparingToTopRightPixel(int[] pixelArray, int arrWidth, int arrHeight) {
		int result[] = new int[arrHeight];
		int topRightColor = pixelArray[arrWidth - 1];
		for (int i = 0; i < arrWidth; i++) {
			for (int j = 0; j < arrHeight; j++) {
				if (pixelArray[j*arrWidth + i] != topRightColor) {
					result[j]++;
				}
			}
		}
		return result;
	}

	private static Map<Integer, CmcPartialCandle> scanVerticallyRemappingToMonochromaticComparingToGivenColor(PixelFlattenedArea pixelFlattenedArea, ValuesAreaColors givenColor) {
		return scanVerticallyRemappingToMonochromaticComparingToGivenColor(pixelFlattenedArea.getPixelArea(), pixelFlattenedArea.getXLength(), pixelFlattenedArea.getYLength(), givenColor);
	}

	/**
	 * Scans a 2D pixel array vertically and remaps the data to a monochromatic representation,
	 * comparing each pixel's value to a given color. The result is an array where each element
	 * represents the count of pixels in each column that are as same as the given color.
	 *
	 * @param pixelArray an array of pixel values representing a 2D image in row-major order
	 * @param arrWidth the width of the 2D pixel grid
	 * @param arrHeight the height of the 2D pixel grid
	 * @param givenColor the color value to compare each pixel against
	 * @return an integer array representing the count of pixels in each column that differ
	 *         from the given color
	 */
	private static Map<Integer, CmcPartialCandle> scanVerticallyRemappingToMonochromaticComparingToGivenColor(int[] pixelArray, int arrWidth, int arrHeight, ValuesAreaColors givenColor) {
		Map<Integer, CmcPartialCandle> result = new HashMap<>();
		int max = -1;
		int min = arrHeight;
		for (int i = 0; i < arrHeight; i++) {
			for (int j = 0; j < arrWidth; j++) {
				if (pixelArray[i*arrWidth + j] == givenColor.getColor()) {
					max = j > max ? j : max;
					min = j < min ? j : min;
				}
			}
			if (max > -1) {
				result.put(i, new CmcPartialCandle(i, min, max, givenColor));
			}
		}
		return result;
	}

	private static String detectTrimmedCharsAndRun(int[] scannedVertically, TextPixelFlattenedArea tpfArea, Map<Integer, Integer> colorsReplacementMap, DetectedTrimmedShapePredicate predicate) {
		return detectTrimmedCharsAndRun(scannedVertically, tpfArea.getPixelArea(), tpfArea.getXLength(), tpfArea.getYLength(), colorsReplacementMap,  predicate);
	}

	// jak sama nazwa wskazuje - ta metoda maksymalnie przycina (horyzontalnie) tablicę - a potem zbiera znaki
	private static String detectTrimmedCharsAndRun(int[] scannedVertically, int[] pixelArray, int arrWidth, int arrHeight, Map<Integer, Integer> colorsReplacementMap, DetectedTrimmedShapePredicate predicate) {
		boolean detected = false;
		int startPointer = 0;
		int charactersCount = 0;
		int gapWhiteSpaceInsertMinLen = 5;
		int gapLength = 0;
		StringBuilder sharedSB = new StringBuilder();
		for (int i = 0; i < scannedVertically.length; i++) {
			if (!detected) {
				if (scannedVertically[i] > 0) {
					if (gapLength >= gapWhiteSpaceInsertMinLen && sharedSB.length() > 0) {
						log.info("Injecting whitespace " + sharedSB);
						sharedSB.append(" ");
					}
					startPointer = i;
					detected = true;
					gapLength = 0;
				} else {
					gapLength++;
				}
				continue;
			}
			if (detected && scannedVertically[i] == 0) {
				charactersCount++;
				detected = false;
				//printArea(new PixelSubarea(startPointer, i-startPointer, pixelArray, arrWidth, arrHeight, "Character " + charactersCount));

				boolean predicateResult = predicate.test(new PixelShapeContainer(startPointer, i-startPointer, pixelArray, arrWidth, arrHeight, "Character " + charactersCount, sharedSB, colorsReplacementMap));
				if (!predicateResult) {
					throw new RuntimeException("While processing detected trimmed character " + charactersCount + " at " + startPointer + " to " + (i-startPointer));
				}
				continue;
			}
			if (detected && i ==  scannedVertically.length - 1) {
				charactersCount++;
				detected = false;
				//printArea(new PixelSubarea(startPointer, i-startPointer+1, pixelArray, arrWidth, arrHeight, "Character " + charactersCount));
				boolean predicateResult = predicate.test(new PixelShapeContainer(startPointer, i-startPointer+1, pixelArray, arrWidth, arrHeight, "Character " + charactersCount, sharedSB, colorsReplacementMap));
				if (!predicateResult) {
					throw new RuntimeException("While processing detected trimmed character " + charactersCount + " at " + startPointer + " to " + (i-startPointer+1));
				}
			}
		}
		if (sharedSB.length() > 0) {
			log.info("Detected shared string in brackets: [" + sharedSB + "]");
		}
		return sharedSB.toString();
	}

	private static String detectCharAndRun(int[] scannedVertically, TextPixelFlattenedArea tpfArea, Map<Integer, Integer> colorsReplacementMap, DetectedTrimmedShapePredicate predicate) {
		return detectCharAndRun(scannedVertically, tpfArea.getPixelArea(), tpfArea.getXLength(), tpfArea.getYLength(), colorsReplacementMap,  predicate);
	}

	// ta metoda nic nie przycina - i zbiera znaki horyzontalnie
	// -> do zbierania pojedynczych, nie-tekstowych znaków
	private static String detectCharAndRun(int[] scannedVertically, int[] pixelArray, int arrWidth, int arrHeight, Map<Integer, Integer> colorsReplacementMap, DetectedTrimmedShapePredicate predicate) {
		StringBuilder sharedSB = new StringBuilder();
		boolean predicateResult = predicate.test(new PixelShapeContainer(0, scannedVertically.length, pixelArray, arrWidth, arrHeight, "Only character ", sharedSB, colorsReplacementMap));
		if (!predicateResult) {
			throw new RuntimeException("While processing detected only character at 0 to " + scannedVertically.length);
		}
		if (sharedSB.length() > 0) {
			log.info("Detected shared string in brackets: [" + sharedSB + "]");
		}
		return sharedSB.toString();
	}

	private static Map<Integer, Integer> getFF121212RecoloringMap() {
		return Map.of(
				// przemapowywanie szarego tła na efektywnie monochromatyczne tło
				-15592941, -15592942,        // -15592941 = 0xff121213 ; #121213 -> #121212 -> #000
				-15592686, -15592942,        // -15592686 = 0xff121312 ; #121312 -> #121212 -> #000
				-15592685, -15592942,        // -15592685 = 0xff121313 ; #121313 -> #121212 -> #000
				-15527406, -15592942,        // -15527406 = 0xff131212 ; #131212 -> #121212 -> #000
				-15527405, -15592942,        // -15527406 = 0xff131212 ; #131213 -> #121212 -> #000
				-15527150, -15592942,        // -15527150 = 0xff131312 ; #131312 -> #121212 -> #000
				-15527149, -15592942        // -15527150 = 0xff131313 ; #131313 -> #121212 -> #000
		);
	}

	private static Map<Integer, Integer> getCurrentLineRecoloringMap() {
		return Map.of(-3487030, -16777216);	// -3487030 = 0xFFCACACA ; #CACACA -> #000
	}
}
