package com.w2e.core.service.docx;

import com.w2e.core.model.DocRow;

import java.util.List;

public interface DocxService {
    <T extends DocRow> List<T> readDocument(String pathToDocument);
}