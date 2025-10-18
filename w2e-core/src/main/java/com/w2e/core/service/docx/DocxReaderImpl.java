package com.w2e.core.service.docx;

import com.w2e.core.config.CoreConfig;
import com.w2e.core.model.DocRow;
import com.w2e.core.model.DocTableCell;
import com.w2e.core.model.DocTableRow;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Builder
public class DocxReaderImpl implements DocReader {
    @NonNull
    private final CoreConfig config;

    @Override
    public <T extends DocRow> List<T> readDocument(String pathToDocument) {

        // If we have n rows and each row has column A
        // then it means that we will take column A values from row 0 till n
        // Could be a map like column idx -> range(7)

        try (InputStream is = Files.newInputStream(Path.of(pathToDocument));
             XWPFDocument doc = new XWPFDocument(is)) {
            List<XWPFTable> tables = doc.getTables();

            if (tables.isEmpty()) {
                String errMsg = String.format("No tables found in the document: [%s].", pathToDocument);
                log.error(errMsg);
                throw new RuntimeException(errMsg);
            }

            if (tables.size() > 1) {
                String errMsg = String.format("Document [%s] has more than one table.", pathToDocument);
                log.error(errMsg);
                throw new RuntimeException(errMsg);
            }

            List<DocTableRow> docTableRowList = new ArrayList<>();
            for (XWPFTable table : tables) {
                log.info("\nTransposing table rows of the document [{}]...", pathToDocument);
                docTableRowList.addAll(transposeColRangeToRow(table));
                log.info("Transposing table rows of the document [{}] is done.\n", pathToDocument);
            }
            return (List<T>) docTableRowList;

        } catch (IOException e) {
            log.error("Exception happened when reading document: [{}]", pathToDocument, e);
            throw new RuntimeException(e);
        }

    }

    private List<DocTableRow> transposeColRangeToRow(XWPFTable table) {
        List<DocTableRow> docTableRowList = new ArrayList<>();
        int numberOfRows = table.getNumberOfRows();
        int cellToReadPos = config.getWordDoc().getColData().getColPos();
        int rowStartPos = config.getWordDoc().getColData().getColRange().getRowStartPos();
        int rowEndPos = config.getWordDoc().getColData().getColRange().getRowEndPos();
        int columnDataRange = rowEndPos - rowStartPos;

        // Specific number of rows in document will become
        // column in destination excel file
        List<DocTableCell> docTableCellList = new ArrayList<>();
        int destCellStartPos = 1;
        int destRowPos = 0;
        int rowIdx = 0;
        int emptyRowCount = 0;
        // Loop via rows in document
        for (int rowNum = 0; rowNum < numberOfRows; rowNum++) {
            XWPFTableRow row = table.getRow(rowNum);
            if (isEmptyRow(row)) {
                emptyRowCount++;
                continue;
            }
            rowIdx++;
            //TODO: Loop via existing cells
            XWPFTableCell cell = row.getCell(cellToReadPos);
            docTableCellList.add(DocTableCell.builder()
                    .cellPos(destCellStartPos++)
                    .text(cell.getText())
                    .build());

            if (rowIdx % columnDataRange == 0) {
                docTableRowList.add(DocTableRow.builder()
                        .rowNum(destRowPos++)
                        .cellList(List.copyOf(docTableCellList))
                        .build());
                docTableCellList = new ArrayList<>();
                destCellStartPos = 1;
            }

        }

        log.info("Total rows in original document table: {}", numberOfRows);
        log.info("Empty rows in original document table: {}", emptyRowCount);
        log.info("Rows in transposed table: {}", docTableRowList.size());
        return docTableRowList;
    }

    private boolean isEmptyRow(XWPFTableRow row) {
        boolean isEmpty = true;
        if (row == null) {
            return isEmpty;
        }
        for (XWPFTableCell cell : row.getTableCells()) {
            // If at least one cell is not empty the row is not empty
            if (cell != null && StringUtils.isNotEmpty(cell.getText())) {
                return !isEmpty;
            }
        }
        return isEmpty;
    }
}
