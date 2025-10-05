package com.w2e.core.service.converter;

import com.w2e.core.model.DocTableRow;
import com.w2e.core.service.docx.DocxService;
import com.w2e.core.service.excel.ExcelService;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Comparator;
import java.util.List;

@Builder
@ToString
@Getter
public class W2EConverterImpl implements  W2EConverter {
    private DocxService docxService;
    private ExcelService excelService;

    @Override
    public void convert( String docPath, String pathToExcel ) {
        List<DocTableRow> docRowLst = docxService.readDocument(docPath);
        List<DocTableRow> sortedDocRowList = docRowLst.stream()
                .sorted(Comparator.comparing(DocTableRow::getRowNum))
                .toList();
        excelService.writeToExcel(pathToExcel, sortedDocRowList);
    }

    @Override
    public void convert( List<String> docPathList, String pathToExcel ) {
        for (String docPath: docPathList) {
            convert(docPath, pathToExcel);
        }
    }
}
