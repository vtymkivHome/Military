package com.w2e;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DocDevCLITest {
    private DocDevCLI docDevCLI;

    @BeforeEach
    void setUp() {
        docDevCLI = new DocDevCLI();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void run() {
        String[] args = {"--pathToDoc=src/test/resources/input_data/record_example.docx"
                , "--pathToExcel=src/test/resources/output_data/journal.xlsx"};
        docDevCLI.run(args);
    }
}