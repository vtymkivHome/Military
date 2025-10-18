package com.w2e.ui.task;

import com.w2e.core.W2ECLI;
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
            W2ECLI w2ECLI = new W2ECLI();
            try {
                for (File file : sourceFileList) {
                    fileName = file.getName();
                    updateMessage(fileName);
                    // Convert
                    w2ECLI.convertDocToExcel(file.getAbsolutePath(), destinationFile, pathToConfig);
                    totalFilesConverted++;
                    updateProgress(totalFilesConverted, allFilesCount);

                }
            }  catch (Exception e) {
                String errMsg = String.format("Error happened during converting file %s: %s", fileName, e.getMessage());
                updateMessage("Conversion failed!");
                throw new IllegalArgumentException(errMsg); // Re-throw to handle in onFailed
            }
            updateMessage("Files converting completed!");
            return null;
        }
    }