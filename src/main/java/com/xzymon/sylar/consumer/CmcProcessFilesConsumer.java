package com.xzymon.sylar.consumer;

import com.xzymon.sylar.helper.PathsDto;
import com.xzymon.sylar.model.CmcRawDataContainer;
import com.xzymon.sylar.service.CmcBufferedImageProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Component
@Slf4j
public class CmcProcessFilesConsumer implements ProcessFilesConsumer {

	private final CmcBufferedImageProcessingService imageProcessingService;

	public CmcProcessFilesConsumer(CmcBufferedImageProcessingService imageProcessingService) {
		this.imageProcessingService = imageProcessingService;
	}

	@Override
	public void accept(PathsDto pathsDto) {
		try {
			log.info(String.format("Processing file: %1$s", pathsDto.getPathToInputFile().getFileName().toString()));
			CmcRawDataContainer rawDataContainer = processSingleFileForPath(pathsDto.getPathToInputFile());
			log.info(String.format("File %1$s processed.", pathsDto.getPathToInputFile().getFileName().toString()));
			moveFile(pathsDto.getPathToInputFile(), pathsDto.getLoadingDirectoryProcessed(), rawDataContainer.getPngFileNewName());
			log.info(String.format("File %1$s moved to processed directory.", pathsDto.getPathToInputFile().getFileName().toString()));
			//storeInCsvFile(pathsDto.getGeneratedCsvDirectory(), csvOutput);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private CmcRawDataContainer processSingleFileForPath(Path inputPath) throws IOException {
		log.info("Processing file: " + inputPath.toString());
		BufferedImage image = ImageIO.read(new File(inputPath.toString()));
		return imageProcessingService.extractRawDataFromImage(image, inputPath);
	}
}
