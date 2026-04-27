package com.xzymon.sylar.helper;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.file.Path;

@Data
@AllArgsConstructor
public class PathsDto {
    private String loadingDirectoryProcessed;
    private String loadingDirectoryRenamed;
    private String generatedCsvDirectory;
    private String generatedPngDirectory;
    private Path pathToInputFile;
}
