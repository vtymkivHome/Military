package com.w2e.core.service.docx;

import com.w2e.core.config.CoreConfig;
import com.w2e.core.service.config.ConfigLoaderImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class DocxServiceImplTest {
    private DocxService docxService;

    @BeforeEach
    void setUp() {
        String pathToConfig = "src/test/resources/config/w2e.yml";
        docxService = DocxServiceImpl.builder()
                .config(ConfigLoaderImpl.builder().build().loadConfiguration(pathToConfig, CoreConfig.class))
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void readDocument() {
        String pathToDoc = "src/test/resources/input_data/test.docx";
        docxService.readDocument(pathToDoc);
    }
}