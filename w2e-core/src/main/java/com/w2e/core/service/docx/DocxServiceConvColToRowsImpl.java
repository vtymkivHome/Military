package com.w2e.core.service.docx;


import com.w2e.core.model.DocTableCell;
import com.w2e.core.model.DocTableRow;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Builder
public class DocxServiceConvColToRowsImpl implements DocxService {
    private int numOfRowsToColumns;

    @Override
    public List<DocTableRow> readDocument(String pathToDocument) {
        List<DocTableRow> docTableRowList = new ArrayList<>();
        numOfRowsToColumns = 7;
        try (InputStream is = Files.newInputStream(Paths.get(pathToDocument));
             XWPFDocument doc = new XWPFDocument(is)) {
            List<XWPFTable> tables = doc.getTables();

            if (tables.isEmpty()) {
                String errMsg = "No tables found in the document.";
                log.error(errMsg);
                throw new RuntimeException(errMsg);
            }

            for (int i = 0; i < tables.size(); i++) {
                XWPFTable table = tables.get(i);
                log.debug("\n--- Table N%{}---", i + 1);

                // Read sequentially number of rows that would be converted into
                // Excel columns
                int numberOfRows = table.getNumberOfRows();
                //XWPFTableRow firstRow = Optional.ofNullable(table.getRow(0)).orElseThrow(() -> new RuntimeException("Table does contain rows"));

                int cell_to_read = 1;
                // Covert columns to rows

                // Number of columns will become number of row and number of rows will become
                // number off columns. Number of row is limited to 7

                List<DocTableCell> docTableCellList = new ArrayList<>();
                int newCell = 1;
                int newRow = 0;
                int rowIdx = 0;
                // Loop via rows in document
                for (int rowNum = 0; rowNum < numberOfRows; rowNum++) {
                    XWPFTableRow row = table.getRow(rowNum);
                    if (isEmptyRow(row)) {
                        continue;
                    }
                    rowIdx++;
                    if (rowIdx % numOfRowsToColumns == 0) {
                        docTableRowList.add(DocTableRow.builder()
                                .rowNum(newRow++)
                                .cellList(docTableCellList)
                                .build());
                        docTableCellList = new ArrayList<>();
                        newCell = 1;

                    } else {
                        XWPFTableCell cell = row.getCell(cell_to_read);
                        docTableCellList.add(DocTableCell.builder()
                                .cellPos(newCell++)
                                .text(cell.getText())
                                .build());
                    }

                }
                // In case if have odd number of rows
                if (CollectionUtils.isNotEmpty(docTableCellList)) {
                    docTableRowList.add(DocTableRow.builder()
                            .rowNum(newRow++)
                            .cellList(docTableCellList)
                            .build());
                }
                log.info("Rows in table: {}", docTableRowList.size());

            }

        } catch (IOException e) {
            log.error("Exception happened when reading document: [{}]", pathToDocument, e);
            throw new RuntimeException(e);
        }
        return docTableRowList;
    }

    private boolean isEmptyRow(XWPFTableRow row) {
        if (row == null) {
            return true;
        }
        for (XWPFTableCell cell : row.getTableCells()) {
            if (cell != null && !cell.toString().trim().isEmpty()) {
                return false;
            }
        }
        return false;
    }
}
