package com.w2e.core.service.excel;

import com.w2e.core.model.DocTableRow;

import java.util.List;

public interface ExcelService {
    void writeToExcel(String pathToExcelFile, List<DocTableRow> docTableRowList);

    void writeToExcelByTemplate(List<DocTableRow> docTableRowList, String pathToExcelTemplateFile, String pathToExcelOutputFile);

    void deleteRows(String pathToExcelFile, int startRow, int endRow);
}
