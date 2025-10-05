package com.w2e.core.service.converter;

import java.util.List;

public interface W2EConverter {
    void convert( String docPath, String pathToExcel );
    void convert(List<String> docPathList, String pathToExcel );
}
