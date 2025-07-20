package com.xzymon.sylar.consumer;

import com.xzymon.sylar.helper.PathsDto;
import com.xzymon.sylar.model.FrameCoords;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class CmcProcessFilesConsumer implements ProcessFilesConsumer {
    @Override
    public void accept(PathsDto pathsDto) throws IOException {
        log.info(String.format("Processing file: %1$s", pathsDto.getPathToInputFile().getFileName().toString()));
        //CsvOutput csvOutput = processSingleFileForPath(pathsDto.getPathToInputFile());
        storeInPngFile(pathsDto.getPathToInputFile(), pathsDto.getGeneratedPngDirectory());
        log.info(String.format("File %1$s processed.", pathsDto.getPathToInputFile().getFileName().toString()));
        moveFile(pathsDto.getPathToInputFile(), pathsDto.getLoadingDirectoryProcessed());
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

    private void storeInPngFile(Path inputPath, String genPngDir) throws IOException {
        BufferedImage image = ImageIO.read(new File(inputPath.toString()));

        FrameCoords snapshotDateTimeFC = new FrameCoords( 9, 253, 37, 33);
        int[] snapshotDateTimePixels = extractFrameAsImage("snapshotDateTime", snapshotDateTimeFC, genPngDir, image);

        int arrWidth = snapshotDateTimeFC.getRight() - snapshotDateTimeFC.getLeft();
        int arrHeight = snapshotDateTimeFC.getBottom() - snapshotDateTimeFC.getTop();
        int[] scannedVertically = scanVertically(snapshotDateTimePixels, arrWidth, arrHeight);
        printTrimmedChars(scannedVertically, snapshotDateTimePixels, arrWidth, arrHeight);
        //printArea(0, 12, snapshotDateTimePixels, arrWidth, arrHeight, "First char");

        /*
        FrameCoords valorNameFC = new FrameCoords( 165, 174, 200, 31);
        int[] valorNamePixels = extractFrameAsImage("valorName", valorNameFC, genPngDir, image);

        FrameCoords intervalFC = new FrameCoords( 1941, 114, 1992, 66);
        int[] intervalPixels = extractFrameAsImage("interval", intervalFC, genPngDir, image);

        FrameCoords valuesFC = new FrameCoords( 381, 1852, 1897, 0);
        int[] valuesPixels = extractFrameAsImage("values", valuesFC, genPngDir, image);

        FrameCoords valuesAloneFC = new FrameCoords( 413, 1740, 1890, 10);
        int[] valuesAlonePixels = extractFrameAsImage("valuesAlone", valuesAloneFC, genPngDir, image);

        FrameCoords verticalGaugesFC = new FrameCoords( 381, 1852, 406, 0);
        int[] verticalGaugesPixels = extractFrameAsImage("verticalGauges", verticalGaugesFC, genPngDir, image);

        FrameCoords horizontalGaugesFC = new FrameCoords( 414, 1852, 1890, 1739);
        int[] horizontalGaugesPixels = extractFrameAsImage("horizontalGauges", horizontalGaugesFC, genPngDir, image);
         */
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

    private static int[] scanVertically(int[] pixelArray, int arrWidth, int arrHeight) {
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

    private static void printTrimmedChars(int[] scannedVertically, int[] pixelArray, int arrWidth, int arrHeight) {
        boolean detected = false;
        int startPointer = 0;
        int charactersCount = 0;
        for (int i = 0; i < scannedVertically.length; i++) {
            if (!detected && scannedVertically[i] > 0) {
                startPointer = i;
                detected = true;
                continue;
            }
            if (detected && scannedVertically[i] == 0) {
                charactersCount++;
                detected = false;
                printArea(startPointer, i-startPointer, pixelArray, arrWidth, arrHeight, "Character " + charactersCount);
                continue;
            }
            if (detected && i ==  scannedVertically.length - 1) {
                charactersCount++;
                detected = false;
                printArea(startPointer, i-startPointer+1, pixelArray, arrWidth, arrHeight, "Character " + charactersCount);
            }
        }
    }

    private static void printArea(int offset, int areaWidth, int[] pixelArray, int arrWidth, int arrHeight, String message) {
        //check array params correctness
        if (pixelArray == null) {
            log.info("pixelArray is null");
            return;
        }
        if (arrWidth <= 0) {
            log.info("arrWidth is no more than 0");
            return;
        }
        if (arrHeight <= 0) {
            log.info("arrHeight is no more than 0");
            return;
        }
        if (pixelArray.length != arrWidth * arrHeight) {
            log.info("pixelArray is {}", pixelArray.length);
            log.info("pixelArray length != arrWidth * arrHeight");
            return;
        }
        if (offset >= arrWidth) {
            log.info("offset >= arrWidth");
            return;
        }
        if (areaWidth > arrWidth) {
            log.info("areaWidth > arrWidth");
            return;
        }
        log.info(message);
        int[] subArea = new int[areaWidth*arrHeight];
        int currentPixelIndex = 0;
        int subareaPixelIndex = 0;
        int lineStartIndex;
        while (subareaPixelIndex < areaWidth*arrHeight) {
            lineStartIndex = currentPixelIndex;
            currentPixelIndex += offset;
            for (int i = 0; i < areaWidth; i++) {
                //log.info("currentPixelIndex={}, lineStartIndex={}, i={}", currentPixelIndex, lineStartIndex, i);
                subArea[subareaPixelIndex+i] = pixelArray[currentPixelIndex+i];
            }
            subareaPixelIndex += areaWidth;
            currentPixelIndex = lineStartIndex + arrWidth;
        }
        printAreaHumanReadable(areaWidth, arrHeight, subArea);
        printAreaFlatenedMonoBinary(areaWidth, arrHeight, subArea);
    }

    private static void printAreaHumanReadable(int areaWidth, int arrHeight, int[] subArea) {
        for (int i = 0; i < arrHeight; i++) {
            for (int j = 0; j < areaWidth; j++) {
                printMonoPixel(subArea[i* areaWidth + j], subArea[0]);
            }
            System.out.println();
        }
    }

    private static void printAreaFlatenedMonoBinary(int areaWidth, int arrHeight, int[] subArea) {
        System.out.println("width: " + areaWidth);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arrHeight; i++) {
            for (int j = 0; j < areaWidth; j++) {
                sb.append(getMonoBinary(subArea[i* areaWidth + j], subArea[0])).append(",");
            }
        }
        System.out.println("public static final int[] CHAR_1 = {" + sb + "};");
    }

    private static void printMonoPixel(int pixel, int backgroundColor) {
        char color = pixel == backgroundColor ? ' ' : '#';
        System.out.print(color);
    }

    private static char getMonoBinary(int pixel, int backgroundColor) {
        return pixel == backgroundColor ? '0' : '1';
    }
}
