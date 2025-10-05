package com.w2e.core.service.docx;

//import org.army.model.DocumentContext;

import java.util.List;

public interface DocxService {
    <T> List<T> readDocument(String pathToDocument);
}