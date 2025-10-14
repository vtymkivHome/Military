package com.w2e.core.service.docx;


import com.w2e.core.config.CoreConfig;
import com.w2e.core.model.DocTableCell;
import com.w2e.core.model.DocTableRow;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
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
public class DocxServiceImpl implements DocxService {
    private CoreConfig config;

    @Override
    public List<DocTableRow> readDocument(String pathToDocument) {
        int shift_rows = config.getExcelDoc().getShiftRows();
        List<DocTableRow> docTableRowList = new ArrayList<>();

        try (InputStream is = Files.newInputStream(Paths.get(pathToDocument));
             XWPFDocument doc = new XWPFDocument(is)) {
            List<XWPFTable> tables = doc.getTables();

            if (tables.isEmpty()) {
                String errMsg = String.format("No tables found in the document [%s].", pathToDocument);
                log.error(errMsg);
                throw new RuntimeException(errMsg);
            }

            for (int i = 0; i < tables.size(); i++) {
                XWPFTable table = tables.get(i);
                log.debug("\n--- Table N%{}---", i + 1);

                // Read sequentially number of rows that would be converted into
                // excel columns
                int numberOfRows = table.getNumberOfRows();

                for(int rowNum = shift_rows; rowNum < numberOfRows; rowNum++) {
                    XWPFTableRow row = table.getRow(rowNum);
                    int cellSize = row.getTableCells().size();
                    List<DocTableCell> docTableCellList = new ArrayList<>(cellSize);
                    for(int cellPos = 0; cellPos < cellSize; cellPos++) {
                        XWPFTableCell cell = row.getCell(cellPos);
                        docTableCellList.add(DocTableCell.builder()
                                        .cellPos(cellPos)
                                        .text(cell.getText())
                                .build());
                    }

                    docTableRowList.add(DocTableRow.builder()
                            .rowNum(rowNum)
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
}
