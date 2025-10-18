package com.w2e.core.service.excel;

import com.w2e.core.config.CoreConfig;
import com.w2e.core.model.DocTableCell;
import com.w2e.core.model.DocTableRow;
import com.w2e.core.model.W2ESysProp;
import com.w2e.core.service.config.ConfigLoaderImpl;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class ExcelServiceImplTest {

    @Test
    void writeToExcel() {
        List<DocTableRow> docTableRowList = getDocTableRows();
        String pathToExcelFile = "src/test/resources/output_data/test.xlsx";
        String pathToConfig = W2ESysProp.CONFIG_FILE_PATH.getDefaultPath();

        ExcelService excelService = ExcelServiceImpl.builder()
                .config(ConfigLoaderImpl.builder().build().loadConfiguration(pathToConfig, CoreConfig.class))
                .build();
        excelService.deleteRows(pathToExcelFile, 3, -1);
        excelService.writeToExcel(pathToExcelFile, docTableRowList);
    }


    private List<DocTableRow> getDocTableRows() {
        List<DocTableRow> docTableRowList = new ArrayList<>();
        int maxRowNum = 4;
        for (int rowNum = 0; rowNum < maxRowNum; rowNum++) {
            int maxCellNum = 3;
            List<DocTableCell> cellList = new ArrayList<>(maxCellNum);
            for (int cellPos = 0; cellPos < maxCellNum; cellPos++) {
                DocTableCell docTableCell = DocTableCell.builder()
                        .cellPos(cellPos)
                        .text(String.format("Row num: %s Cell num: %s", rowNum, cellPos))
                        .build();
                cellList.add(docTableCell);
            }
            docTableRowList.add(DocTableRow.builder()
                    .rowNum(rowNum)
                    .cellList(cellList)
                    .build());
        }

        return docTableRowList;
    }
}