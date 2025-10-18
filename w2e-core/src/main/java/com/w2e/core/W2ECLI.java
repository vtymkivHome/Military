package com.w2e.core;

import com.w2e.core.config.CoreConfig;
import com.w2e.core.factory.DocReaderFactory;
import com.w2e.core.model.InputParameter;
import com.w2e.core.service.config.ConfigLoaderImpl;
import com.w2e.core.service.converter.W2EConverter;
import com.w2e.core.service.converter.W2EConverterImpl;
import com.w2e.core.service.docx.DocReader;
import com.w2e.core.service.excel.ExcelService;
import com.w2e.core.service.excel.ExcelServiceImpl;
import com.w2e.core.service.parameter.ParameterException;
import com.w2e.core.service.parameter.ParameterServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class W2ECLI {
    public static void main(String[] args) {
        new W2ECLI().run(args);
    }

    public void run(String[] args) {
        InputParameter inputParameter;
        try {
            inputParameter = ParameterServiceImpl.builder().build().loadInputParameter(args, () -> "templates/journal_template.xlsx");
        } catch (ParameterException e) {
            throw new RuntimeException(e);
        }
        log.info("Input parameters:{}", inputParameter);
        convertDocToExcel(List.of(inputParameter.getPathToDoc()), inputParameter.getPathToExcel(), inputParameter.getConfigFilePath());
    }

    public void convertDocToExcel(List<String> docPathList, String pathToExcel, String pathToConfig) {
        for (String docPath : docPathList) {
            convertDocToExcel(docPath, pathToExcel, pathToConfig);
        }
    }

    public void convertDocToExcel(String docPath, String pathToExcel, String pathToConfig) {
        CoreConfig config = ConfigLoaderImpl.builder().build().loadConfiguration(pathToConfig, CoreConfig.class);
        DocReader docReader = DocReaderFactory.builder()
                .config(config)
                .build()
                .getReader(docPath);

        ExcelService excelService = ExcelServiceImpl.builder()
                .config(config)
                .build();

        W2EConverter w2EConverter = W2EConverterImpl.builder()
                .docReader(docReader)
                .excelService(excelService)
                .build();
        w2EConverter.convert(docPath, pathToExcel);
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