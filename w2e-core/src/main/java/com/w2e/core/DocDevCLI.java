package com.w2e.core;

import com.w2e.core.model.InputParameter;
import com.w2e.core.service.converter.W2EConverter;
import com.w2e.core.service.converter.W2EConverterImpl;
import com.w2e.core.service.docx.DocxServiceImpl;
import com.w2e.core.service.excel.ExcelService;
import com.w2e.core.service.excel.ExcelServiceImpl;
import com.w2e.core.service.parameter.ParameterException;
import com.w2e.core.service.parameter.ParameterServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DocDevCLI {
    public static void main(String[] args) {
        new DocDevCLI().run(args);
    }

    public void run(String[] args) {
        InputParameter inputParameter;
        try {
            inputParameter = ParameterServiceImpl.builder().build().loadInputParameter(args);
        } catch (ParameterException e) {
            throw new RuntimeException(e);
        }
       convertDocToExcel(List.of(inputParameter.getPathToDoc()), inputParameter.getPathToExcel());
    }

    public void convertDocToExcel(List<String> docPathList, String pathToExcel) {
        ExcelService excelService = ExcelServiceImpl.builder()
                .mapDocCellToExcelCel(getMappingDocCellToExcelCell())
                .build();

        W2EConverter w2EConverter = W2EConverterImpl.builder()
                 .docxService(DocxServiceImpl.builder().build())
                 .excelService(excelService)
                 .build();
         w2EConverter.convert(docPathList, pathToExcel);
     }

    private Map<Integer, Integer> getMappingDocCellToExcelCell() {
        // TODO: Define mapping in config file
        // Map that identifies what row from document table would be mapped to what cell

        Map<Integer, Integer> mapDocCellToExcelCel = new HashMap<>();
        mapDocCellToExcelCel.put(0, 0); // 1 -> 1
        mapDocCellToExcelCel.put(1, 1); // 2
        mapDocCellToExcelCel.put(2, 2); // 3
        mapDocCellToExcelCel.put(3, 3); // 4
        mapDocCellToExcelCel.put(4, 4); // 5
        mapDocCellToExcelCel.put(5, 5); // 6
        mapDocCellToExcelCel.put(6, 6); // 7
        mapDocCellToExcelCel.put(7, 7); // 8
        return mapDocCellToExcelCel;
    }
}