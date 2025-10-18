package com.w2e.core.service.docx;

import com.w2e.core.model.DocRow;

import java.util.List;

public interface DocReader {
    /**
     * @param pathToDocument The path to the word document to read from
     * @return List of rows from document table
     */
    <T extends DocRow> List<T> readDocument(String pathToDocument);
}
