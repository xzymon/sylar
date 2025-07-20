package com.xzymon.sylar.scheduling;

import com.xzymon.sylar.consumer.CmcProcessFilesConsumer;
import com.xzymon.sylar.consumer.ProcessFilesConsumer;
import com.xzymon.sylar.consumer.StqProcessFilesConsumer;
import com.xzymon.sylar.helper.PathsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

	private static final String DIR_STQ_IN_PATH_PROBLEM_HINT = "Property loading.directory.stq.in problem. Should be Read-Write directory. Hint: ";
	private static final String DIR_STQ_PROC_PATH_PROBLEM_HINT = "Property loading.directory.stq.processed problem. Should be Read-Write directory. Hint: ";
	private static final String DIR_STQ_GEN_PATH_PROBLEM_HINT = "Property generatedCSV.directory.stq.out problem. Should be Read-Write directory. Hint: ";

	private static final String DIR_CMC_IN_PATH_PROBLEM_HINT = "Property loading.directory.stq.in problem. Should be Read-Write directory. Hint: ";
	private static final String DIR_CMC_PROC_PATH_PROBLEM_HINT = "Property loading.directory.stq.processed problem. Should be Read-Write directory. Hint: ";
	private static final String DIR_CMC_GEN_CSV_PATH_PROBLEM_HINT = "Property generatedCSV.directory.stq.out problem. Should be Read-Write directory. Hint: ";
	private static final String DIR_CMC_GEN_PNG_PATH_PROBLEM_HINT = "Property generatedPNG.directory.stq.out problem. Should be Read-Write directory. Hint: ";

	@Value("${loading.directory.stq.in}")
	private String loadingDirectoryStqIn;

	@Value("${loading.directory.stq.processed}")
	private String loadingDirectoryStqProcessed;

	@Value("${generatedCSV.directory.stq.out}")
	private String stqGeneratedCsvDirectory;

	@Value("${loading.directory.cmc.in}")
	private String loadingDirectoryCmcIn;

	@Value("${loading.directory.cmc.processed}")
	private String loadingDirectoryCmcProcessed;

	@Value("${generatedCSV.directory.cmc.out}")
	private String cmcGeneratedCsvDirectory;

	@Value("${generatedPNG.directory.cmc.out}")
	private String cmcGeneratedPngDirectory;

	@Value("${mainScheduler.enabled}")
	private boolean mainSchedulerEnabled;

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	private StqProcessFilesConsumer stqProcessFilesConsumer;
	private CmcProcessFilesConsumer cmcProcessFilesConsumer;

	public MainScheduler(StqProcessFilesConsumer stqProcessFilesConsumer,  CmcProcessFilesConsumer cmcProcessFilesConsumer) {
		this.stqProcessFilesConsumer = stqProcessFilesConsumer;
		this.cmcProcessFilesConsumer = cmcProcessFilesConsumer;
	}

	//@Scheduled(fixedRate = 60 * 1000)
	@Scheduled(cron = "0 * * * * *")
	public void invokeScheduledMethodAtFixedRate() {
		if (mainSchedulerEnabled) {
			LOGGER.info("The time is now {}", dateFormat.format(new Date()));
			processStqFiles();
			processCmcFiles();
		}
	}

	private void processStqFiles() {
		checkDirectory(loadingDirectoryStqIn, DIR_STQ_IN_PATH_PROBLEM_HINT);
		checkDirectory(loadingDirectoryStqProcessed, DIR_STQ_PROC_PATH_PROBLEM_HINT);
		checkDirectory(stqGeneratedCsvDirectory, DIR_STQ_GEN_PATH_PROBLEM_HINT);
		LOGGER.info(loadingDirectoryStqIn);
		processFiles(loadingDirectoryStqIn, loadingDirectoryStqProcessed, stqGeneratedCsvDirectory, null, stqProcessFilesConsumer);
	}

	private void processCmcFiles() {
		checkDirectory(loadingDirectoryCmcIn, DIR_CMC_IN_PATH_PROBLEM_HINT);
		checkDirectory(loadingDirectoryCmcProcessed, DIR_CMC_PROC_PATH_PROBLEM_HINT);
		checkDirectory(cmcGeneratedCsvDirectory, DIR_CMC_GEN_CSV_PATH_PROBLEM_HINT);
		checkDirectory(cmcGeneratedPngDirectory, DIR_CMC_GEN_PNG_PATH_PROBLEM_HINT);
		LOGGER.info(loadingDirectoryCmcIn);
		processFiles(loadingDirectoryCmcIn, loadingDirectoryCmcProcessed, cmcGeneratedCsvDirectory, cmcGeneratedPngDirectory, cmcProcessFilesConsumer);
	}

	private void processFiles(String loadingDirectoryIn,
							  String loadingDirectoryProcessed,
							  String generatedCsvDirectory,
							  String generatedPngDirectory,
							  ProcessFilesConsumer processFilesConsumer) {
		Path path = Paths.get(loadingDirectoryIn);
		List<Path> pathsToFiles;
		try {
			pathsToFiles = Files.list(path)
					.filter(p -> !Files.isDirectory(p))
					.collect(Collectors.toUnmodifiableList());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (!pathsToFiles.isEmpty()) {
			for (Path pathToFile : pathsToFiles) {
				try {
					processFilesConsumer.accept(new PathsDto(loadingDirectoryProcessed, generatedCsvDirectory, generatedPngDirectory, pathToFile));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			LOGGER.info("No files to process.");
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
