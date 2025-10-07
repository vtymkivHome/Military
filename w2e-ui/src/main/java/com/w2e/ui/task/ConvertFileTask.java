package com.w2e.ui.task;

import com.w2e.core.DocDevCLI;
import javafx.concurrent.Task;
    import java.io.File;
    import java.io.FileInputStream;
    import java.io.FileOutputStream;
    import java.io.IOException;
import java.util.List;

public class ConvertFileTask extends Task<Void> {
        private final List<File> sourceFileList;
        private final String destinationFile;

        public ConvertFileTask(List<File> sourceFileList, String destinationFile) {
            this.sourceFileList = sourceFileList;
            this.destinationFile = destinationFile;
        }

        @Override
        protected Void call() {
            long allFilesCount = sourceFileList.size();
            long totalFilesConverted = 0;
            File currentFile = null;
            DocDevCLI docDevCLI = new DocDevCLI();
            try {
                for (File file : sourceFileList) {
                    updateMessage(file.getAbsolutePath());
                    currentFile = file;
                    // Convert
                    docDevCLI.convertDocToExcel(currentFile.getAbsolutePath(), destinationFile);
                    totalFilesConverted++;
                    updateProgress(totalFilesConverted, allFilesCount);
                    //updateValue(currentFile);

                }
            }  catch (Exception e) {
                String errMsg = String.format("Error during converting file %s: %s", currentFile.getName(), e.getMessage());
                updateMessage(errMsg);
                throw new IllegalArgumentException(errMsg); // Re-throw to handle in onFailed
            }
            updateMessage("Files converting completed!");
            return null;
        }
    }