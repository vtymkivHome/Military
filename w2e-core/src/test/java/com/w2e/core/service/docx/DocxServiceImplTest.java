package com.w2e.core.service.docx;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class DocxServiceImplTest {
    String pathToDoc = "src/test/resources/input_data/test.docx";
    DocxService docxService;
    @BeforeEach
    void setUp() {
        docxService = DocxServiceImpl.builder().build();
    }

    @AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void readDocument() {
        docxService.readDocument(pathToDoc);
    }
}