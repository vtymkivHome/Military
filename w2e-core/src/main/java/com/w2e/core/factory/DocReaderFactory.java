package com.w2e.core.factory;

import com.w2e.core.config.CoreConfig;
import com.w2e.core.service.docx.DocReader;
import com.w2e.core.service.docx.DocReaderImpl;
import com.w2e.core.service.docx.DocxReaderImpl;
import lombok.Builder;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

@Builder
public class DocReaderFactory {
    @NonNull
    private final CoreConfig config;

    public DocReader getReader(String pathToDoc) {
        if (StringUtils.isAllBlank(pathToDoc)) {
            throw new IllegalArgumentException("Path to document cannot be null or empty");
        }
        if (!pathToDoc.endsWith(".doc") && !pathToDoc.endsWith(".docx")) {
            throw new IllegalArgumentException(String.format("The document [%s] is not in Word format.", pathToDoc));
        }

        if (pathToDoc.endsWith(".doc")) {
            return DocReaderImpl.builder().config(config).build();
        }
        if (pathToDoc.endsWith(".docx")) {
            return DocxReaderImpl.builder().config(config).build();
        }
        return null;
    }
}
