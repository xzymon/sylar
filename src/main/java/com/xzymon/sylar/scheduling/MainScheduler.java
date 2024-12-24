package com.xzymon.sylar.scheduling;

import com.xzymon.sylar.processing.StockPngPaletteImageProcessor;
import com.xzymon.sylar.model.RawDataContainer;
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

	@Value("${loading.directory.in}")
	private String loadingDirectoryIn;

	@Value("${loading.directory.processed}")
	private String loadingDirectoryProcessed;

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	//@Scheduled(fixedRate = 60 * 1000)
	@Scheduled(cron = "0 * * * * *")
	public void reportCurrentTime() {
		LOGGER.info("The time is now {}", dateFormat.format(new Date()));
		LOGGER.info(loadingDirectoryIn);
		checkDirectory(loadingDirectoryIn, DIR_IN_PATH_PROBLEM_HINT);
		checkDirectory(loadingDirectoryProcessed, DIR_PROC_PATH_PROBLEM_HINT);
		processFiles(loadingDirectoryIn);
	}

	private void processFiles(String loadingDirectoryIn) {
		Path path = Paths.get(loadingDirectoryIn);
		List<Path> pathsToFiles;
		try {
			pathsToFiles = Files.list(path)
					.filter(p -> !Files.isDirectory(p))
					.collect(Collectors.toUnmodifiableList());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		for (Path pathsToFile : pathsToFiles) {
			try {
				processSingleFileForPath(pathsToFile);
			} catch (IOException e) {

			}
		}
	}

	private void processSingleFileForPath(Path path) throws IOException {
		PngImage png = PngImage.read(new File(path.toString()));
		BufferedPaletteImage buffPalImg = (BufferedPaletteImage) ImageDecoder.toImage(png);
		StockPngPaletteImageProcessor stockImageProcessor = new StockPngPaletteImageProcessor();
		RawDataContainer container = stockImageProcessor.process(buffPalImg);
		Path processedPath = Paths.get(loadingDirectoryProcessed);
		Files.move(path, processedPath);
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
