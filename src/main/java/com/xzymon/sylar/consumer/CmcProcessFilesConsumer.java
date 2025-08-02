package com.xzymon.sylar.consumer;

import com.xzymon.sylar.constants.IntervalHelper;
import com.xzymon.sylar.constants.MonthPlMapping;
import com.xzymon.sylar.constants.ValorNameHelper;
import com.xzymon.sylar.constants.marker.MarkerCharacter;
import com.xzymon.sylar.predicate.*;
import com.xzymon.sylar.helper.PathsDto;
import com.xzymon.sylar.model.CmcRawDataContainer;
import com.xzymon.sylar.model.FrameCoords;
import com.xzymon.sylar.model.PixelShapeContainer;
import com.xzymon.sylar.model.TextPixelFlattenedArea;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
@Slf4j
public class CmcProcessFilesConsumer implements ProcessFilesConsumer {

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

            if (rawDataContainer.isToolboxFlag()) {
                rawDataContainer.setNarrowFlag(true);
            } else {
                // check wide
                FrameCoords wideToolboxMarkerFC = new FrameCoords(1910, 163, 1940, 133);
                determineIfIsToolboxMarker(rawDataContainer, wideToolboxMarkerFC, image);
                rawDataContainer.setWideFlag(rawDataContainer.isToolboxFlag());
            }

            if (rawDataContainer.isToolboxFlag()) {
                FrameCoords intervalLine1FC = new FrameCoords(1941, 101, 1963, 66);
                extractIntervalLine1AreaInDataContainer(rawDataContainer, intervalLine1FC, image);

                FrameCoords intervalLine2FC = new FrameCoords(1974, 113, 1991, 66);
                extractIntervalLine2AreaInDataContainer(rawDataContainer, intervalLine2FC, image);
            } else {
                FrameCoords intervalOptionsFC = new FrameCoords(1906, 81, 1926, 22);
                extractIntervalOptionsAreaInDataContainer(rawDataContainer, intervalOptionsFC, image);
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

    private static String detectTrimmedCharsAndRun(int[] scannedVertically, TextPixelFlattenedArea tpfArea, Map<Integer, Integer> colorsReplacementMap, DetectedTrimmedShapePredicate predicate) {
        return detectTrimmedCharsAndRun(scannedVertically, tpfArea.getPixelArea(), tpfArea.getXLength(), tpfArea.getYLength(), colorsReplacementMap,  predicate);
    }

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
}
