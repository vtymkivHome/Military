package com.w2e.ui.task;

import com.w2e.core.DocDevCLI;
import javafx.concurrent.Task;

import java.io.File;
import java.util.List;

public class ConvertFileTask extends Task<Void> {
        private final List<File> sourceFileList;
        private final String destinationFile;
    private final String pathToConfig;

    public ConvertFileTask(List<File> sourceFileList, String destinationFile, String pathToConfig) {
            this.sourceFileList = sourceFileList;
            this.destinationFile = destinationFile;
        this.pathToConfig = pathToConfig;

        }

        @Override
        protected Void call() {
            long allFilesCount = sourceFileList.size();
            long totalFilesConverted = 0;
            String fileName = "";
            DocDevCLI docDevCLI = new DocDevCLI();
            try {
                for (File file : sourceFileList) {
                    fileName = file.getName();
                    updateMessage(fileName);
                    // Convert
                    docDevCLI.convertDocToExcel(file.getAbsolutePath(), destinationFile, pathToConfig);
                    totalFilesConverted++;
                    updateProgress(totalFilesConverted, allFilesCount);

                }
            }  catch (Exception e) {
                String errMsg = String.format("Error during converting file %s: %s", fileName, e.getMessage());
                updateMessage(errMsg);
                throw new IllegalArgumentException(errMsg); // Re-throw to handle in onFailed
            }
            updateMessage("Files converting completed!");
            return null;
        }
    }