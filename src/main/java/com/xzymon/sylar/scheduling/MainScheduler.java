package com.xzymon.sylar.scheduling;

import com.xzymon.sylar.model.CsvOutput;
import com.xzymon.sylar.model.RawDataContainer;
import com.xzymon.sylar.service.BarStockPngPaletteImageProcessingService;
import com.xzymon.sylar.service.StockPngPaletteImageProcessingService;
import io.nayuki.png.ImageDecoder;
import io.nayuki.png.PngImage;
import io.nayuki.png.image.BufferedPaletteImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Configuration
public class MainScheduler {
	private static final Logger LOGGER = LoggerFactory.getLogger(MainScheduler.class);

	private static final String DIR_IN_PATH_PROBLEM_HINT = "Property loading.directory.in problem. Should be Read-Write directory. Hint: ";
	private static final String DIR_PROC_PATH_PROBLEM_HINT = "Property loading.directory.processed problem. Should be Read-Write directory. Hint: ";
	private static final String DIR_GEN_PATH_PROBLEM_HINT = "Property generatedCSV.directory.out problem. Should be Read-Write directory. Hint: ";

	@Value("${loading.directory.in}")
	private String loadingDirectoryIn;

	@Value("${loading.directory.processed}")
	private String loadingDirectoryProcessed;

	@Value("${generatedCSV.directory.out}")
	private String generatedCsvDirectory;

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	private StockPngPaletteImageProcessingService stockImageProcessingService;

	public MainScheduler(StockPngPaletteImageProcessingService stockImageProcessingService) {
		this.stockImageProcessingService = stockImageProcessingService;
	}

	//@Scheduled(fixedRate = 60 * 1000)
	@Scheduled(cron = "0 * * * * *")
	public void reportCurrentTime() {
		LOGGER.info("The time is now {}", dateFormat.format(new Date()));
		LOGGER.info(loadingDirectoryIn);
		checkDirectory(loadingDirectoryIn, DIR_IN_PATH_PROBLEM_HINT);
		checkDirectory(loadingDirectoryProcessed, DIR_PROC_PATH_PROBLEM_HINT);
		checkDirectory(generatedCsvDirectory, DIR_GEN_PATH_PROBLEM_HINT);
		processFiles(loadingDirectoryIn);
	}

	private void processFiles(String loadingDirectoryIn) {
		Path path = Paths.get(loadingDirectoryIn);
		List<Path> pathsToFiles;
		CsvOutput csvOutput;
		try {
			pathsToFiles = Files.list(path)
					.filter(p -> !Files.isDirectory(p))
					.collect(Collectors.toUnmodifiableList());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		for (Path pathToFile : pathsToFiles) {
			try {
				LOGGER.info(String.format("Processing file: %1$s", pathToFile.getFileName().toString()));
				csvOutput = processSingleFileForPath(pathToFile);
				LOGGER.info(String.format("File %1$s processed.", pathToFile.getFileName().toString()));
				moveFile(pathToFile);
				LOGGER.info(String.format("File %1$s moved to processed directory.", pathToFile.getFileName().toString()));
				storeInCsvFile(generatedCsvDirectory, csvOutput);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private CsvOutput processSingleFileForPath(Path path) throws IOException {
		PngImage png = PngImage.read(new File(path.toString()));
		BufferedPaletteImage buffPalImg = (BufferedPaletteImage) ImageDecoder.toImage(png);
		BarStockPngPaletteImageProcessingService stockImageProcessor = new BarStockPngPaletteImageProcessingService();
		RawDataContainer container = stockImageProcessor.extractRawDataFromImage(buffPalImg);
		return stockImageProcessor.toCsvOutput(container);
	}

	private void moveFile(Path path) {
		Path processedDirPath = Paths.get(loadingDirectoryProcessed);
		Path movedFilePath =  processedDirPath.resolve(path.getFileName());
		try {
			Files.move(path, movedFilePath);
		} catch (IOException e) {
			LOGGER.error("Error when moving file {}", path.getFileName().toString());
			throw new RuntimeException(e);
		}
	}

	private void storeInCsvFile(String outputDir, CsvOutput csvOutput) {
		Path path = Paths.get(outputDir, csvOutput.getFileName());
		LOGGER.info(String.format("Will try to save file: %1$s", csvOutput.getFileName()));
		if (Files.exists(path)) {
			LOGGER.info(String.format("File %1$s already exists. Can't create file!!!", csvOutput.getFileName()));
			return;
		}
		try {
			Files.createFile(path);
			Files.write(path, csvOutput.getContent());
			LOGGER.info(String.format("File %1$s created and content saved.", csvOutput.getFileName()));
		} catch (IOException e) {
			LOGGER.error("Error when writing to file {}", csvOutput.getContent());
		}
	}

	private void checkDirectory(String directoryPath, String loggerHint) {
		Path path = Paths.get(directoryPath);
		if (!Files.exists(path)) {
			LOGGER.info(loggerHint + "directory does not exist.");
			return;
		}
		if (!Files.isDirectory(path)) {
			LOGGER.info(loggerHint + "is not a directory.");
			return;
		}
		if (!Files.isReadable(path)) {
			LOGGER.info(loggerHint + "is not readable.");
			return;
		}
		if (!Files.isWritable(path)) {
			LOGGER.info(loggerHint + "is not writable.");
			return;
		}
		try {
			Files.list(path)
					.filter(p -> !Files.isDirectory(p))
					.forEach(p -> LOGGER.info(String.format("file: %1$s", p.getFileName().toString())));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
