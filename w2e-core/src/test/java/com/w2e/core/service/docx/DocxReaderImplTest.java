package com.w2e.core.service.docx;

import com.w2e.core.config.CoreConfig;
import com.w2e.core.model.W2ESysProp;
import com.w2e.core.service.config.ConfigLoaderImpl;
import org.junit.jupiter.api.BeforeEach;

class DocxReaderImplTest {
    private DocReader docxReader;

    @BeforeEach
    void setUp() {
        String pathToConfig = W2ESysProp.CONFIG_FILE_PATH.getPath();
        docxReader = DocxReaderImpl.builder()
                .config(ConfigLoaderImpl.builder().build().loadConfiguration(pathToConfig, CoreConfig.class))
                .build();
    }

    @org.junit.jupiter.api.Test
    void readDocument() {
        String pathToDoc = "src/test/resources/input_data/test.docx";
        docxReader.readDocument(pathToDoc);
    }
}