package com.example.accessingdataneo4j.utils;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
public class FileUtils {

    /**
     * Retrieves all Excel files in the given directory and its subdirectories,
     * that match the provided filter.
     *
     * @param directory The directory containing the Excel files.
     * @param excelFileFilter The filter to select only Excel files.
     * @return A List of File objects representing the matching Excel files.
     */
    public static List<File> getExcelFiles(File directory, FilenameFilter excelFileFilter) {
        List<File> excelFiles = new ArrayList<>();
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    excelFiles.addAll(getExcelFiles(file, excelFileFilter));
                } else {
                    if (excelFileFilter.accept(file, file.getName())) {
                        excelFiles.add(file);
                    }
                }
            }
        }

        return excelFiles;
    }
}
