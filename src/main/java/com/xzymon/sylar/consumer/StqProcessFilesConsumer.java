package com.xzymon.sylar.consumer;

import com.xzymon.sylar.helper.PathsDto;
import com.xzymon.sylar.model.CsvOutput;
import com.xzymon.sylar.model.StqRawDataContainer;
import com.xzymon.sylar.service.StockPngPaletteImageProcessingService;
import io.nayuki.png.ImageDecoder;
import io.nayuki.png.PngImage;
import io.nayuki.png.image.BufferedPaletteImage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Component
@Slf4j
public class StqProcessFilesConsumer implements ProcessFilesConsumer {
    private StockPngPaletteImageProcessingService stockImageProcessingService;

    public StqProcessFilesConsumer(StockPngPaletteImageProcessingService stockImageProcessingService) {
        this.stockImageProcessingService = stockImageProcessingService;
    }

    @Override
    public void accept(PathsDto pathsDto) {
        try {
            log.info(String.format("Processing file: %1$s", pathsDto.getPathToInputFile().getFileName().toString()));
            StqRawDataContainer rawDataContainer = processSingleFileForPath(pathsDto.getPathToInputFile());
            CsvOutput csvOutput = stockImageProcessingService.toCsvOutput(rawDataContainer);
            log.info(String.format("File %1$s processed.", pathsDto.getPathToInputFile().getFileName().toString()));
            moveFile(pathsDto.getPathToInputFile(), pathsDto.getLoadingDirectoryProcessed(), null);
            log.info(String.format("File %1$s moved to processed directory.", pathsDto.getPathToInputFile().getFileName().toString()));
            storeInCsvFile(pathsDto.getGeneratedCsvDirectory(), csvOutput);
        } catch (IOException e) {
            //log.error("Error when processing file {}", pathsDto.getPathToInputFile().getFileName().toString());
            throw new RuntimeException(e);
        }
    }

    private StqRawDataContainer processSingleFileForPath(Path path) throws IOException {
        PngImage png = PngImage.read(new File(path.toString()));
        BufferedPaletteImage buffPalImg = (BufferedPaletteImage) ImageDecoder.toImage(png);
        return stockImageProcessingService.extractRawDataFromImage(buffPalImg);
    }
}
