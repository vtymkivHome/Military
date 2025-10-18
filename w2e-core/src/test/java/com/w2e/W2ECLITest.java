package com.w2e;


import com.w2e.core.W2ECLI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class W2ECLITest {
    private W2ECLI docDevCLI;

    @BeforeEach
    void setUp() {
        docDevCLI = new W2ECLI();
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