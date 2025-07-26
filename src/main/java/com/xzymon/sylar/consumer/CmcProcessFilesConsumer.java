package com.xzymon.sylar.consumer;

import com.xzymon.sylar.function.DetectedTrimmedShapePredicate;
import com.xzymon.sylar.function.PrintTrimmedMonoShapePredicate;
import com.xzymon.sylar.function.ReportingUnknownTrimmedShapePredicate;
import com.xzymon.sylar.helper.PathsDto;
import com.xzymon.sylar.model.FrameCoords;
import com.xzymon.sylar.model.PixelShapeContainer;
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
    public void accept(PathsDto pathsDto) {
        log.info(String.format("Processing file: %1$s", pathsDto.getPathToInputFile().getFileName().toString()));
        //CsvOutput csvOutput = processSingleFileForPath(pathsDto.getPathToInputFile());
        try {
            storeInPngFile(pathsDto.getPathToInputFile(), pathsDto.getGeneratedPngDirectory());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        log.info("Processing file: " + inputPath.toString());
        BufferedImage image = ImageIO.read(new File(inputPath.toString()));

        FrameCoords snapshotDateTimeFC = new FrameCoords( 9, 253, 37, 33);
        int[] snapshotDateTimePixels = extractFrameAsImage("snapshotDateTime", snapshotDateTimeFC, genPngDir, image);

        int arrWidth = snapshotDateTimeFC.getRight() - snapshotDateTimeFC.getLeft();
        int arrHeight = snapshotDateTimeFC.getBottom() - snapshotDateTimeFC.getTop();
        int[] scannedVertically = scanVerticallyRemappingToMonochromaticComparingToTopLeftPixel(snapshotDateTimePixels, arrWidth, arrHeight);
        //detectTrimmedCharsAndRun(scannedVertically, snapshotDateTimePixels, arrWidth, arrHeight, new PrintTrimmedMonoShapePredicate());
        detectTrimmedCharsAndRun(scannedVertically, snapshotDateTimePixels, arrWidth, arrHeight, new ReportingUnknownTrimmedShapePredicate());
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

    private static void detectTrimmedCharsAndRun(int[] scannedVertically, int[] pixelArray, int arrWidth, int arrHeight, DetectedTrimmedShapePredicate predicate) {
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
                //printArea(new PixelSubarea(startPointer, i-startPointer, pixelArray, arrWidth, arrHeight, "Character " + charactersCount));

                boolean predicateResult = predicate.test(new PixelShapeContainer(startPointer, i-startPointer, pixelArray, arrWidth, arrHeight, "Character " + charactersCount));
                if (!predicateResult) {
                    throw new RuntimeException("While processing detected trimmed character " + charactersCount + " at " + startPointer + " to " + (i-startPointer));
                }
                continue;
            }
            if (detected && i ==  scannedVertically.length - 1) {
                charactersCount++;
                detected = false;
                //printArea(new PixelSubarea(startPointer, i-startPointer+1, pixelArray, arrWidth, arrHeight, "Character " + charactersCount));
                boolean predicateResult = predicate.test(new PixelShapeContainer(startPointer, i-startPointer+1, pixelArray, arrWidth, arrHeight, "Character " + charactersCount));
                if (!predicateResult) {
                    throw new RuntimeException("While processing detected trimmed character " + charactersCount + " at " + startPointer + " to " + (i-startPointer+1));
                }
            }
        }
    }
}
