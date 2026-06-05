package com.xzymon.sylar.service;

import com.xzymon.sylar.constants.framecoords.CmcFrameCoordsConstants;
import com.xzymon.sylar.constants.IntervalHelper;
import com.xzymon.sylar.constants.MonthPlMapping;
import com.xzymon.sylar.constants.ValorNameHelper;
import com.xzymon.sylar.constants.marker.MarkerCharacter;
import com.xzymon.sylar.helper.CmcPartialCandlePrescenceRegister;
import com.xzymon.sylar.helper.ColorReplacementHelper;
import com.xzymon.sylar.helper.Segment;
import com.xzymon.sylar.helper.ValuesAreaColors;
import com.xzymon.sylar.model.*;
import com.xzymon.sylar.predicate.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CandleCmcBufferedImageProcessingService implements CmcBufferedImageProcessingService {
	private static final int CHECK_MARGIN = 3;
	private static final int FRAME_LINE_WIDTH = 1;
	private static final int HGAUGE_SAFE_DISTANCE = 31;
	private static final String EMPTY_TEXT_SEGMENT = "!!!EMPTY TEXT SEGMENT!!!";

	@Value("${default.year}")
	private Integer defaultYearForNewFilename;

	@Value("${processing.files.rename}")
	private boolean renameFiles;

	@Override
	public CmcRawDataContainer extractRawDataFromImage(BufferedImage image, Path inputPath) throws IOException {
		CmcRawDataContainer rawDataContainer = new CmcRawDataContainer();

		rawDataContainer.setSourceFileName(inputPath.getFileName().toString());

		extractSnapshotDateTimeAreaInDataContainer(rawDataContainer, CmcFrameCoordsConstants.SNAPSHOT_DATETIME, image);
		determineIfIsNotChartMarker(rawDataContainer, CmcFrameCoordsConstants.NOT_CHART_MARKER, image);

		// dalsze pobieranie danych nie ma sensu jeżeli to jednak nie jest prawidłowy obraz z wykresem
		if (!rawDataContainer.isNotChart()) {
			extractValorNameAreaInDataContainer(rawDataContainer, CmcFrameCoordsConstants.VALOR_NAME, image);

			determineIfIsToolboxMarker(rawDataContainer, CmcFrameCoordsConstants.NARROW_TOOLBOX_MARKER, image);

			// toolbox flag może się pojawić w różnym miejscu - w zależności który widok jest na zdjęciu
			// najpierw (powyżej) przeprowadzono próbę sprawdzenia czy toolboxMarker jest obecny w położeniu charakterystycznym dla wąskiego wykresu
			// więc poniżej jest sprawdzenie czy flaga została ustawiona -> jeżeli tak to to jest wąski wykres
			if (rawDataContainer.isToolboxFlag()) {
				rawDataContainer.setNarrowFlag(true);
			} else {
				// skoro flaga nie została ustawiona - to teraz bieżemy ustawienia charakterystyczne dla szerokiego wykresu
				// i sprawdzamy czy zostanie wykryty toolboxMarker
				determineIfIsToolboxMarker(rawDataContainer, CmcFrameCoordsConstants.WIDE_TOOLBOX_MARKER, image);
				// jeżeli został wykryty toolboxMarker to ustawiamy flagę szerokiego wykresu
				rawDataContainer.setWideFlag(rawDataContainer.isToolboxFlag());
			}

			if (rawDataContainer.isToolboxFlag()) {
				//jeżeli toolboxFlag jest ustawiony, to mamy do czynienia z napisem w 2 rzędach - więc próbujemy zmapować znaki z 2 rzędów
				extractIntervalLine1AreaInDataContainer(rawDataContainer, CmcFrameCoordsConstants.INTERVAL_LINE_1, image);

				extractIntervalLine2AreaInDataContainer(rawDataContainer, CmcFrameCoordsConstants.INTERVAL_LINE_2, image);
			} else {
				// jeżeli nie jest ustawiony toolboxFlag - to znaczy że widok zawiera napis "Opcje" i interwał jest zapisany w jednym wierszu
				// - zatem należy zmapować kod interwału dla znanego charakterystycznego położenia tego kodu
				extractIntervalOptionsAreaInDataContainer(rawDataContainer, CmcFrameCoordsConstants.INTERVAL_OPTIONS, image);
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
			log.debug("!rawDataContainer.isNotChart()");
			if (rawDataContainer.isToolboxFlag()) {
				log.debug("!rawDataContainer.isNotChart() && rawDataContainer.isToolboxFlag()");
				if (rawDataContainer.isNarrowFlag()) {
					log.debug("!rawDataContainer.isNotChart() && rawDataContainer.isToolboxFlag() && rawDataContainer.isNarrowFlag()");
					determineValuesFrameCoordsByMatchingCorners(rawDataContainer, CmcFrameCoordsConstants.DEFAULT_NARROW_TOOLBOX_VALUES_FRAME, image, CHECK_MARGIN);
				}
				if (rawDataContainer.isWideFlag()) {
					log.debug("!rawDataContainer.isNotChart() && rawDataContainer.isToolboxFlag() && rawDataContainer.isWideFlag()");
					determineValuesFrameCoordsByMatchingCorners(rawDataContainer, CmcFrameCoordsConstants.DEFAULT_WIDE_TOOLBOX_VALUES_FRAME, image, CHECK_MARGIN);
				}
			} else {
				// po prostu testujemy czy któreś wpadnie - a jak wpadnie to ustawianie odpowiedniej flagi
				determineValuesFrameCoordsByMatchingCorners(rawDataContainer, CmcFrameCoordsConstants.DEFAULT_NARROW_VALUES_FRAME, image, CHECK_MARGIN);
				if (rawDataContainer.isValuesFrameFlag()) {
					log.info("setting narrow flag");
					rawDataContainer.setNarrowFlag(true);
				} else {
					determineValuesFrameCoordsByMatchingCorners(rawDataContainer, CmcFrameCoordsConstants.DEFAULT_WIDE_VALUES_FRAME, image, CHECK_MARGIN);
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
				extractPartialCandles(rawDataContainer, image);
				//TODO - do końca if
				constructCandles(rawDataContainer);
				// mapowanie położenia na osi X na czas
				extractTextOnTimeAxis(rawDataContainer, image);
				fillTimeAxisWithTimeReferencePoints(rawDataContainer);
				bindCandlesToTimeReferencePoints(rawDataContainer);
				// mapowanie połozenia na osi Y na wartość
				bindCandlesToValueAxis(rawDataContainer);
			}
		}

		return rawDataContainer;
	}

	private void constructCandles(CmcRawDataContainer rawDataContainer) {
		log.info("constructCandles - no implementation");

	}

	private void extractTextOnTimeAxis(CmcRawDataContainer rawDataContainer, BufferedImage image) throws IOException {
		log.info("extractTextOnTimeAxis");
		if (rawDataContainer.isNarrowFlag()) {
			rawDataContainer.setTimeAxisFC(CmcFrameCoordsConstants.DEFAULT_NARROW_TIME_AXIS_FRAME);
		}
		if (rawDataContainer.isWideFlag()){
			rawDataContainer.setTimeAxisFC(CmcFrameCoordsConstants.DEFAULT_WIDE_TIME_AXIS_FRAME);
		}
		FrameCoords frameCoords = rawDataContainer.getTimeAxisFC();
		if (frameCoords != null) {
			TextPixelFlattenedArea timeAxisArea = rawDataContainer.getTimeAxisArea();
			//snapshotDateTimeArea.setPixelArea(extractFrameAsImage("snapshotDateTime", rawDataContainer.getSnapshotDateTimeFC(), genPngDir, image));
			timeAxisArea.setPixelArea(extractPixelsFromFrame(rawDataContainer.getTimeAxisFC(), image));
			timeAxisArea.setXLength(frameCoords.getRight() - frameCoords.getLeft());
			timeAxisArea.setYLength(frameCoords.getBottom() - frameCoords.getTop());
			int[] scannedVertically = scanVerticallyRemappingToMonochromaticComparingToTopLeftPixel(timeAxisArea);
			//scannedVertically - z tej tablicy można wnioskować jak powinna zostać rozbita przestrzeń z frameCoords
			int minSeparatorGap = 14; //arbitralnie - zobaczymy co z tego wyjdzie
			List<Segment> segments = new ArrayList<>();
			int[] segmentsRegister = new int[scannedVertically.length];
			int previousNotGapIndex = -1;
			int gapLength;
			// będziemy przechodzić przez scannedVertically i w segmentsRegister "pokolorujemy" "piksele" gdzie był tekst.
			// jeżeli to była przerwa (scannedVertically[i]==0) to pozostaje 0, jeżeli nie przerwa (>0) to ustawiamy segmentsRegister[i]=1.
			// No więc przechodzimy przez piksele i gdy po "pikselu" będącym przerwą mamy "piksel" który przerwą nie jest - to patrzymy wstecz
			// i jeżeli poprzedni "piksel" nie będący przerwą był bliżej niż minSeparatorGap - to "piksele" między nimi
			// oznaczamy teraz jako "nie przerwę" (czyli 1) - i w ten sposób zasypujemy te "zbyt krótkie przerwy"
			// dzięki czemu otrzymujemy spójne obszary gdzie segmentsRegister[i] = 1 - i to są obszary do zdekonstruowania tekstu.
			for (int i = 0; i < scannedVertically.length; i++) {
				if (scannedVertically[i] > 0) {
					segmentsRegister[i] = 1;
					if (previousNotGapIndex != -1) {
						gapLength = i - (previousNotGapIndex + 1);
						if (gapLength > 0 && gapLength < minSeparatorGap) {
							for (int j = previousNotGapIndex + 1; j < i; j++) {
								segmentsRegister[j] = 1;
							}
						}
					}
					previousNotGapIndex = i;
				}
			}
			// teraz będziemy przechodzić przez segmentsRegister i tworzyć obiekty reprezentujące segmenty tekstu
			// one będą stanowić materiał na bazie którego potem wydobędziemy teksty na osi czasu nad wykresem
			int currentSegmentStart = -1;
			int currentSegmentEnd = -1;
			Segment segment = null;
			for (int i = 0; i < segmentsRegister.length; i++) {
				if (segmentsRegister[i] == 1) {
					if (currentSegmentStart == -1) {
						currentSegmentStart = i;
					}
					currentSegmentEnd = i;
				} else {
					if (currentSegmentStart != -1) {
						segment = new Segment(currentSegmentStart, currentSegmentEnd);
						segments.add(segment);
						log.info("text segment on time axis: " + segment);
						currentSegmentStart = -1;
						currentSegmentEnd = -1;
					}
				}
			}
			// na wypadek gdyby ostatni segment tekstu dotykał końca obszaru
			if (currentSegmentStart != -1) {
				segment = new Segment(currentSegmentStart, currentSegmentEnd);
				segments.add(segment);
				log.info("text segment on time axis: " + segment);
			}
			log.info("segments count: {}", segments.size());
			if (segments.size() > 0) {
				extractTimeAxisTextAreas(rawDataContainer, image, segments);
			} else {
				log.error("No time axis text areas found, skipping extraction");
				throw new RuntimeException("No time axis text areas found, skipping extraction");
			}
		} else {
			log.error("Time axis frame coordinates are null, skipping snapshot date time extraction");
			throw new RuntimeException("Time axis frame coordinates are null, skipping snapshot date time extraction");
		}
	}

	private void extractTimeAxisTextAreas(CmcRawDataContainer rawDataContainer, BufferedImage image, List<Segment> segments) throws IOException {
		log.info("extractTimeAxisTextAreas");
		FrameCoords timeAxisFC = rawDataContainer.getTimeAxisFC();
		for (Segment segment : segments) {
			FrameCoords segmentFC = new FrameCoords(timeAxisFC.getTop(), timeAxisFC.getLeft() + segment.last(), timeAxisFC.getBottom(), timeAxisFC.getLeft() + segment.first());
			TextPixelFlattenedArea segmentArea =  new TextPixelFlattenedArea();
			rawDataContainer.getTimeAxisTextAreaMap().put(segment.first(), segmentArea);
			//NOTE: tu było setPixelArea - ale została zmodyfikowana kolejność - ze względu na konieczność HACK!
			segmentArea.setXLength(segmentFC.getRight() - segmentFC.getLeft());
			segmentArea.setYLength(segmentFC.getBottom() - segmentFC.getTop());
			String detectedText;
			if (segmentArea.getXLength() == 0) {
				//HACK! - gdy wykres zaczyna się na pionowej prowadnicy - nad prowadnicą nie ma miejsca na zmieszczenie tekstu czasu - i jest pusty!
				// ja sobie tu na to po prostu ustawiam stałą reprezentującą tą sytuację
				detectedText = EMPTY_TEXT_SEGMENT;
			} else {
				segmentArea.setPixelArea(extractPixelsFromFrame(segmentFC, image));
				int[] scannedVertically = scanVerticallyRemappingToMonochromaticComparingToTopLeftPixel(segmentArea);
				detectedText = detectTrimmedCharsAndRun(scannedVertically, segmentArea, Map.of(), new AdvancedRecoloring30FontSize17ReportingUnknownTrimmedShapePredicate());
			}
			segmentArea.setExtractedText(detectedText);
			log.info("extracted text for Time Axis Area: {}", detectedText);
		}
	}

	private void fillTimeAxisWithTimeReferencePoints(CmcRawDataContainer rawDataContainer) {
		log.info("fillTimeAxisWithTimeReferencePoints - no implementation");
	}

	private void bindCandlesToTimeReferencePoints(CmcRawDataContainer rawDataContainer) {
		log.info("bindCandlesToTimeReferencePoints - no implementation");
	}

	private void bindCandlesToValueAxis(CmcRawDataContainer rawDataContainer) {
		log.info("bindCandlesToValueAxis - no implementation");
	}

	@Override
	public CsvOutput toCsvOutput(CmcRawDataContainer container) {
		return null;
	}

	private static void preparePngNewName(CmcRawDataContainer rawDataContainer) {
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
		toolboxMarkerArea.setPixelArea(recolorAllWithMap(ColorReplacementHelper.BARELY_BLACK_DEVIATIONS, pixelArea));
		toolboxMarkerArea.setXLength(frameCoords.getRight() - frameCoords.getLeft());
		toolboxMarkerArea.setYLength(frameCoords.getBottom() - frameCoords.getTop());
		int[] scannedVertically = scanVerticallyRemappingToMonochromaticComparingToTopLeftPixel(toolboxMarkerArea);
		try {
			String detectedText = detectTrimmedCharsAndRun(scannedVertically, toolboxMarkerArea, ColorReplacementHelper.NO_REPLACEMENTS, new MarkerReportingUnknownTrimmedShapePredicate());
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
		String detectedText = detectTrimmedCharsAndRun(scannedVertically, valorNameArea, ColorReplacementHelper.NO_REPLACEMENTS, new FontSize27ReportingUnknownTrimmedShapePredicate());
		String trimmedText = detectedText.trim();
		valorNameArea.setExtractedText(trimmedText);
	}

	private static void extractIntervalLine1AreaInDataContainer(CmcRawDataContainer rawDataContainer, FrameCoords frameCoords, BufferedImage image) throws IOException {
		rawDataContainer.setIntervalLine1FC(frameCoords);
		TextPixelFlattenedArea intervalLine1Area = rawDataContainer.getIntervalLine1Area();
		int[] pixelArea = extractPixelsFromFrame(rawDataContainer.getIntervalLine1FC(), image);
		intervalLine1Area.setPixelArea(recolorAllWithMap(ColorReplacementHelper.BARELY_BLACK_DEVIATIONS, pixelArea));
		intervalLine1Area.setXLength(frameCoords.getRight() - frameCoords.getLeft());
		intervalLine1Area.setYLength(frameCoords.getBottom() - frameCoords.getTop());
		int[] scannedVertically = scanVerticallyRemappingToMonochromaticComparingToTopLeftPixel(intervalLine1Area);
		String detectedText = detectTrimmedCharsAndRun(scannedVertically, intervalLine1Area, ColorReplacementHelper.NO_REPLACEMENTS, new FontSize22ReportingUnknownTrimmedShapePredicate());
		String trimmedText = detectedText.trim();
		intervalLine1Area.setExtractedText(trimmedText);
	}

	private static void extractIntervalLine2AreaInDataContainer(CmcRawDataContainer rawDataContainer, FrameCoords frameCoords, BufferedImage image) throws IOException {
		rawDataContainer.setIntervalLine2FC(frameCoords);
		TextPixelFlattenedArea intervalLine2Area = rawDataContainer.getIntervalLine2Area();
		int[] pixelArea = extractPixelsFromFrame(rawDataContainer.getIntervalLine2FC(), image);
		intervalLine2Area.setPixelArea(recolorAllWithMap(ColorReplacementHelper.BARELY_BLACK_DEVIATIONS, pixelArea));
		intervalLine2Area.setXLength(frameCoords.getRight() - frameCoords.getLeft());
		intervalLine2Area.setYLength(frameCoords.getBottom() - frameCoords.getTop());
		int[] scannedVertically = scanVerticallyRemappingToMonochromaticComparingToTopLeftPixel(intervalLine2Area);
		String detectedText = detectTrimmedCharsAndRun(scannedVertically, intervalLine2Area, ColorReplacementHelper.NO_REPLACEMENTS, new FontSize16ReportingUnknownTrimmedShapePredicate());
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
		String detectedText = detectTrimmedCharsAndRun(scannedVertically, intervalOptionsArea, ColorReplacementHelper.INTERVAL_OPTIONS, new FontSize20ReportingUnknownTrimmedShapePredicate());
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
			log.info("Values frame set to: {}", defaultValuesFrame);
			rawDataContainer.setValuesFrameFlag(true);
		} else {
			log.info("Flags");
			StringBuilder errorsSB = new StringBuilder();
			if (topLeftCornerFlag == false) errorsSB.append("topLeftCornerFlag, ");
			if (topRightCornerFlag == false) errorsSB.append("topRightCornerFlag, ");
			if (bottomRightCornerFlag == false) errorsSB.append("bottomRightCornerFlag, ");
			if (bottomLeftCornerFlag == false) errorsSB.append("bottomLeftCornerFlag");
			throw new RuntimeException("values frame not detected - because of missing corner flags: " + errorsSB);
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
			detectedText = detectCharAndRun(scannedVertically, valuesFrameTopLeftArea, ColorReplacementHelper.INTERVAL_OPTIONS, new AdvancedRecoloring60MarkerReportingUnknownTrimmedShapePredicate());
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
		log.info("Extrapolating vertically (->Y) amongst horizontal gauges : [0] -> top");

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
		Integer pixelDiff;

		/* poprzednio jechało od dołu do góry - teraz niżej aktywny kod robi w odwrotnym kierunku
		for (int i = 1; i < safeRefPoint0; i++) {
			pixelDiff = safeRefPoint0 - i;
			extrapolatedValue = refPoint0Value.add(perPixel.multiply(new BigDecimal(i)));
			scaledValue = extrapolatedValue.setScale(3, RoundingMode.HALF_UP);
			hvMap.put(pixelDiff, scaledValue);
			log.info("Extrapolated value: [{}]: {}", pixelDiff, hvMap.get(pixelDiff));
		}*/
		for (int i = safeRefPoint0-1; i > 0; i--) {
			pixelDiff = safeRefPoint0 - i;
			extrapolatedValue = refPoint0Value.add(perPixel.multiply(new BigDecimal(i)));
			scaledValue = extrapolatedValue.setScale(3, RoundingMode.HALF_UP);
			hvMap.put(pixelDiff, scaledValue);
			log.info("Extrapolated vertical (->Y) value: [{}]: {}", pixelDiff, hvMap.get(pixelDiff));
		}
	}

	private void interpolateHorizontalValues(List<Integer> hGaugesSafeToProcess, CmcRawDataContainer rawDataContainer) {
		log.info("Interpolating vertically (->Y) amongst horizontal gauges : [gauge 0] -> [gauge {}]", hGaugesSafeToProcess.size() - 1);

		Map<Integer, BigDecimal> hvMap = rawDataContainer.getHorizontalValuesMap();

		// iterujemy przez kolejne pary punktów referencyjnych
		for (int pairIndex = 0; pairIndex < hGaugesSafeToProcess.size() - 1; pairIndex++) {
			Integer safeRefPointCurrent = hGaugesSafeToProcess.get(pairIndex);
			Integer safeRefPointNext = hGaugesSafeToProcess.get(pairIndex + 1);

			log.info("Interpolating vertically (->Y) between horizontal gauges: gauge[{}]: [{}] and gauge[{}]: [{}]", pairIndex, safeRefPointCurrent, pairIndex+1, safeRefPointNext);

			BigDecimal refPointCurrentValue = rawDataContainer.getHorizontalGauges().get(safeRefPointCurrent).getParsedBDValue();
			BigDecimal refPointNextValue = rawDataContainer.getHorizontalGauges().get(safeRefPointNext).getParsedBDValue();

			// w mianowniku ułamka odejmowane są położenia pikseli: safeRefPointNext - safeRefPointCurrent
			// bo indeksy narastają odwrotnie do kierunku w jakim narastają wartości
			// (położenia(=indeksy) rosną "w dół", wartości rosną "w górę" na wykresie)
			// tu potrzebna jest jedynie wartość różnicy - żeby była dodatnia trzeba odejmować w tym kierunku (no bo musi być większe minus mniejsze)
			// można by też robić absolute - ale po co dokładać dodatkową operację ?
			BigDecimal perPixel = refPointCurrentValue.subtract(refPointNextValue).divide(new BigDecimal(safeRefPointNext - safeRefPointCurrent), 5, RoundingMode.HALF_UP);

			BigDecimal interpolatedValue, scaledValue;
			Integer pixelDiff;

			// interpolujemy wartości między punktami - zaczynamy od następnego po safeRefPointCurrent i kończymy przed safeRefPointNext
			// (bo wartości dla safeRefPointCurrent i safeRefPointNext już są znane z gauges)
			for (int pixelIt = safeRefPointCurrent + 1; pixelIt < safeRefPointNext; pixelIt++) {
				pixelDiff = pixelIt - safeRefPointCurrent;
				interpolatedValue = refPointCurrentValue.subtract(perPixel.multiply(new BigDecimal(pixelDiff)));
				scaledValue = interpolatedValue.setScale(3, RoundingMode.HALF_UP);
				hvMap.put(pixelIt, scaledValue);
				log.info("Interpolated vertival (->Y) value: [{}]: {}", pixelIt, hvMap.get(pixelIt));
			}
		}
	}

	private void extrapolateHorizontalValuesToBottom(List<Integer> hGaugesSafeToProcess, CmcRawDataContainer rawDataContainer) {
		log.info("Extrapolating vertically (->Y) amongst horizontal gauges : [last] -> bottom");

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
		Integer pixelDiff;
		// to jest wysokość wykresu (tzn. rozpiętość pionowa) - zatem jest to bottom - top
		Integer yLength = rawDataContainer.getValuesFrame().getBottom() - rawDataContainer.getValuesFrame().getTop();

		// idziemy od następnego po ostatnim punkcie o znanej wartości -> w dół
		// zatem indeksy rosną, przemnożoną różnicę na piksel odejmujemy od wartości ostatniego znanego punktu
		for (int pixelIt = safeRefPointLast + 1; pixelIt < yLength; pixelIt++) {
			pixelDiff = pixelIt - safeRefPointLast;
			extrapolatedValue = refPointLastValue.subtract(perPixel.multiply(new BigDecimal(pixelDiff)));
			scaledValue = extrapolatedValue.setScale(3, RoundingMode.HALF_UP);
			hvMap.put(pixelIt, scaledValue);
			log.info("Extrapolated vertical (->Y) value: [{}]: {}", pixelIt, hvMap.get(pixelIt));
		}
	}

	private FrameCoords getHorizontalGaugeTextCoords(Integer referencePoint, CmcRawDataContainer rawDataContainer) {
		log.info("referencePoint: {}", referencePoint);
		FrameCoords toolboxNarrowRefPointToTextAreaOffset = new FrameCoords(405, 1852, 427, 1742);

		FrameCoords modifiersFC = null;
		// tylko narrowFlag jest istotny???
		if (/*rawDataContainer.isToolboxFlag() && */rawDataContainer.isNarrowFlag()) {
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
		referencePointsFrameArea.setPixelArea(recolorAllWithMap(ColorReplacementHelper.CURRENT_LEVEL_LINE_ERASER, pixelArea));
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

	private void extractPartialCandles(CmcRawDataContainer rawDataContainer, BufferedImage image) throws IOException {
		FrameCoords frameCoords = rawDataContainer.getValuesFrame();
		PixelFlattenedArea valuesArea = new PixelFlattenedArea();
		valuesArea.setPixelArea(extractPixelsFromFrame(frameCoords, image));
		valuesArea.setXLength(frameCoords.getRight() - frameCoords.getLeft());
		valuesArea.setYLength(frameCoords.getBottom() - frameCoords.getTop());
		log.info("Extracting partial candles");
		rawDataContainer.setAscCoreMap(scanVerticallySearchingForGivenColorVariants(valuesArea, ValuesAreaColors.ASCENDING_CORE));
		rawDataContainer.setAscExtremalMap(scanVerticallySearchingForGivenColorVariants(valuesArea, ValuesAreaColors.ASCENDING_EXTREME));
		rawDataContainer.setDescCoreMap(scanVerticallySearchingForGivenColorVariants(valuesArea, ValuesAreaColors.DESCENDING_CORE));
		rawDataContainer.setDescExtremalMap(scanVerticallySearchingForGivenColorVariants(valuesArea, ValuesAreaColors.DESCENDING_EXTREME));

		CmcPartialCandlePrescenceRegister cmcPartialCandlePrescenceRegister = new CmcPartialCandlePrescenceRegister(valuesArea.getXLength());
		rawDataContainer.setPartialCandlePresenceRegister(cmcPartialCandlePrescenceRegister);

		for (Map.Entry<Integer, CmcPartialCandle> entry : rawDataContainer.getAscCoreMap().entrySet()) {
			cmcPartialCandlePrescenceRegister.put(entry.getValue());
		}
		for (Map.Entry<Integer, CmcPartialCandle> entry : rawDataContainer.getAscExtremalMap().entrySet()) {
			cmcPartialCandlePrescenceRegister.put(entry.getValue());
		}
		for (Map.Entry<Integer, CmcPartialCandle> entry : rawDataContainer.getDescCoreMap().entrySet()) {
			cmcPartialCandlePrescenceRegister.put(entry.getValue());
		}
		for (Map.Entry<Integer, CmcPartialCandle> entry : rawDataContainer.getDescExtremalMap().entrySet()) {
			cmcPartialCandlePrescenceRegister.put(entry.getValue());
		}
		log.info(cmcPartialCandlePrescenceRegister.getStats());

		if (!cmcPartialCandlePrescenceRegister.isCorrect()) {
			throw new RuntimeException("Partial candles are incorrect");
		}

		log.info("Partial candles extraction finished");
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

	private static Map<Integer, CmcPartialCandle> scanVerticallySearchingForGivenColorVariants(PixelFlattenedArea pixelFlattenedArea, ValuesAreaColors mainColor) {
		switch (mainColor) {
			case ASCENDING_CORE -> {
				return scanVerticallySearchingForGivenColorWith1SupportingColors(
						pixelFlattenedArea.getPixelArea(), pixelFlattenedArea.getXLength(), pixelFlattenedArea.getYLength(),
						ValuesAreaColors.ASCENDING_CORE, ValuesAreaColors.CURRENT_LEVEL_ASCENDING_CORE);
			}
			case ASCENDING_EXTREME -> {
				return scanVerticallySearchingForGivenColorWith2SupportingColors(
						pixelFlattenedArea.getPixelArea(), pixelFlattenedArea.getXLength(), pixelFlattenedArea.getYLength(),
						ValuesAreaColors.ASCENDING_EXTREME, ValuesAreaColors.GAUGE_ASCENDING_EXTREME, ValuesAreaColors.CURRENT_LEVEL_ASCENDING_EXTREME);
			}
			case DESCENDING_CORE -> {
				return scanVerticallySearchingForGivenColorWith1SupportingColors(
						pixelFlattenedArea.getPixelArea(), pixelFlattenedArea.getXLength(), pixelFlattenedArea.getYLength(),
						ValuesAreaColors.DESCENDING_CORE, ValuesAreaColors.CURRENT_LEVEL_DESCENDING_CORE);
			}
			case DESCENDING_EXTREME -> {
				return scanVerticallySearchingForGivenColorWith2SupportingColors(
						pixelFlattenedArea.getPixelArea(), pixelFlattenedArea.getXLength(), pixelFlattenedArea.getYLength(),
						ValuesAreaColors.DESCENDING_EXTREME, ValuesAreaColors.GAUGE_DESCENDING_EXTREME, ValuesAreaColors.CURRENT_LEVEL_DESCENDING_EXTREME);
			}
			default -> throw new IllegalArgumentException("Unsupported color variant: " + mainColor);
		}
	}

	/**
	 * W sensie biznesowym istnieją 2 warianty tej metody.
	 * Ten wariant jest dedykowany konkretnie dla poszukiwania knotów świec.
	 *
	 * Ta metoda ma jeszcze wariant zoptymalizowany dla sytuacji, gdy jest tylko jeden kolor pomocniczy.
	 *
	 * Pomysł jest taki: do tej metody trafia cały wykres zmieniony na jednowymiarową tablicę.
	 * Jednowymiarowa tablica pozwala na łatwiejsze iterowanie po kolumnach (X) i wierszach (Y).
	 * Interpretacja jednowymiarowej tablicy jest taka: zawiera włożone piksele kolejno wierszami (z góry w dół, od lewej do prawej).
	 *
	 * 1. dla każdego X (kolumny) sprawdzamy czy w tej kolumnie występuje dany kolor
	 * - jeśli tak, to zapisujemy minimalny i maksymalny Y (wiersz) na którym występuje dany kolor
	 * - znacznikiem tego czy w danej kolumnie znaleziono dany kolor jest wartość max > -1
	 * 2. na koniec tworzymy obiekt CmcPartialCandle i zapisujemy go w mapie
	 *
	 * Parametrami metody są 3 kolory: mainColor, firstSupportingColor, secondSupportingColor
	 * mainColor - to główny kolor, który ma być szukany. Tworzony obiekt CmcPartialCandle będzie scharakteryzowany tym kolorem.
	 * firstSupportingColor - to kolor pomocniczy, którego wystąpienie jest traktowane tak jakby wystąpił ten główny.
	 * secondSupportingColor - analogicznie jak firstSupportingColor, ale dla drugiego koloru pomocniczego.
	 *
	 * Istnienie podziału na 3 kolory wynika z tego, że kolor knota świecy może być przesłonięty białym kolorem wartości bieżącej na wykresie
	 * lub może mieć spod spodu przebitkę prowadnicy poziomej lub pionowej - co zmienia jego kolor, choć na szczęście dalej taki kolor jest
	 * jednoznaczny - tzn. przykładowo jeżeli mamy świecę wznoszącą i kolor konkretnego piksela jej knota jest zmodyfikowany przez kolor prowadnicy
	 * - to i tak jednoznacznie po tym kolorze można rozpoznać, że jest to knot świecy wznoszącej.
	 *
	 * @param pixelArray an 1D array of pixel values representing a 2D image in row-major order
	 * @param arrWidth the width of the 2D pixel grid
	 * @param arrHeight the height of the 2D pixel grid
	 * @param mainColor the color value to compare each pixel against
	 * @param firstSupportingColor the color value to compare each pixel against
	 * @param secondSupportingColor the color value to compare each pixel against
	 * @return an integer array representing the count of pixels in each column that differ
	 *         from the given color
	 */
	private static Map<Integer, CmcPartialCandle> scanVerticallySearchingForGivenColorWith2SupportingColors(int[] pixelArray, int arrWidth, int arrHeight, ValuesAreaColors mainColor, ValuesAreaColors firstSupportingColor, ValuesAreaColors secondSupportingColor) {
		CmcPartialCandle cpc = null;
		Map<Integer, CmcPartialCandle> result = new HashMap<>();
		for (int i = 0; i < arrWidth; i++) { //idzie po X
			int max = -1;
			int min = arrHeight;
			for (int j = 0; j < arrHeight; j++) {  //idzie po Y
				int pixelColor = pixelArray[j*arrWidth + i];
				if (pixelColor == mainColor.getColor()) {
					max = j > max ? j : max;
					min = j < min ? j : min;
				} else {
					if (pixelColor == firstSupportingColor.getColor()) {
						max = j > max ? j : max;
						min = j < min ? j : min;
					} else if (pixelColor == secondSupportingColor.getColor()) {
						max = j > max ? j : max;
						min = j < min ? j : min;
					}
				}
			}
			if (max > -1) {
				cpc = new CmcPartialCandle(i, min, max, mainColor);
				result.put(i, cpc);
				log.info("Found partial candle on X={} : {}", i, cpc);
			}
		}
		return result;
	}

	/**
	 * W sensie biznesowym istnieją 2 warianty tej metody.
	 * Ten wariant jest dedykowany konkretnie dla poszukiwania trzonów świec.
	 *
	 * To zoptymalizowana wersja metody scanVerticallySearchingForGivenColorWith2SupportingColors.
	 * Różni się tym, że zawiera sprawdzenie tylko jednego koloru wspierającego.
	 *
	 * Trzon świecy na wykresie nie pozwala na przebijanie prowadnic. Zatem modyfikacja koloru trzonu jest możliwa tylko
	 * gdy zostanie przesłonięta białym kolorem wartości bieżącej na wykresie.
	 * Z tego widać, że dla trzonu możliwe są tylko 2 warianty koloru - więc i metoda przyjmuje tylko 2 kolory.
	 *
	 * Szerszy opis idei realizowanej przez tą metodę jest dostarczony w opisie wspomnianego drugiego wariantu tej metody.
	 * @see #scanVerticallySearchingForGivenColorWith2SupportingColors(int[], int, int, ValuesAreaColors, ValuesAreaColors, ValuesAreaColors)
	 * @param pixelArray
	 * @param arrWidth
	 * @param arrHeight
	 * @param mainColor
	 * @param firstSupportingColor
	 * @return
	 */
	private static Map<Integer, CmcPartialCandle> scanVerticallySearchingForGivenColorWith1SupportingColors(int[] pixelArray, int arrWidth, int arrHeight, ValuesAreaColors mainColor, ValuesAreaColors firstSupportingColor) {
		CmcPartialCandle cpc = null;
		Map<Integer, CmcPartialCandle> result = new HashMap<>();
		for (int i = 0; i < arrWidth; i++) { //idzie po X
			int max = -1;
			int min = arrHeight;
			for (int j = 0; j < arrHeight; j++) {  //idzie po Y
				int pixelColor = pixelArray[j*arrWidth + i];
				if (pixelColor == mainColor.getColor()) {
					max = j > max ? j : max;
					min = j < min ? j : min;
				} else {
					if (pixelColor == firstSupportingColor.getColor()) {
						max = j > max ? j : max;
						min = j < min ? j : min;
					}
				}
			}
			if (max > -1) {
				cpc = new CmcPartialCandle(i, min, max, mainColor);
				result.put(i, cpc);
				log.info("Found partial candle on X={} : {}", i, cpc);
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

	private static Map<Integer, Integer> getCurrentLineRecoloringMap() {
		return Map.of(-3487030, -16777216);	// -3487030 = 0xFFCACACA ; #CACACA -> #000
	}
}
