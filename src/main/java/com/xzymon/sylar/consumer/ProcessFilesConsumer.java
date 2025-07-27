package com.xzymon.sylar.consumer;

import com.xzymon.sylar.helper.PathsDto;
import com.xzymon.sylar.model.CsvOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@FunctionalInterface
public interface ProcessFilesConsumer extends java.util.function.Consumer<PathsDto> {
    static final Logger LOGGER = LoggerFactory.getLogger(ProcessFilesConsumer.class);

    void accept(PathsDto pathsDto);

    default void moveFile(Path fromPath, String toPathString, String newName) {
        Path processedDirPath = Paths.get(toPathString);
        Path movedFilePath = processedDirPath.resolve(fromPath.getFileName());
        if (newName != null) {
            movedFilePath = processedDirPath.resolve(newName);
        }
        try {
            Files.move(fromPath, movedFilePath);
        } catch (IOException e) {
            LOGGER.error("Error when moving file {}", fromPath.getFileName().toString());
            throw new RuntimeException(e);
        }
    }

    default void storeInCsvFile(String outputDir, CsvOutput csvOutput) {
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
}
