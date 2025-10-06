package com.w2e.core.service.excel;

import com.w2e.core.model.DocTableCell;
import com.w2e.core.model.DocTableRow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;


@Slf4j
@AllArgsConstructor
@Builder
public class ExcelServiceImpl implements ExcelService {
    //TODO: This should e. specified in config file
    private final int ROW_SHIFT = 3;
    // Key is document row cell, value is excel row cell
    private Map<Integer, Integer> mapDocCellToExcelCel;


    @Override
    public void writeToExcel(String pathToExcelFile, List<DocTableRow> docTableRowList) {
        try (FileInputStream fis = new FileInputStream(pathToExcelFile);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(workbook.getActiveSheetIndex());

            Row formatRow = sheet.getRow(sheet.getFirstRowNum());

            int rowNum = sheet.getLastRowNum();

            if (rowNum < ROW_SHIFT) {
                rowNum = ROW_SHIFT;
            }

            for (DocTableRow docTableRow : docTableRowList) {
                Row row = sheet.createRow(rowNum++);
                for(DocTableCell docTableCell : docTableRow.getCellList()) {
                    if(mapDocCellToExcelCel.containsKey(docTableCell.getCellPos())) {
                        Integer excelCellPos = mapDocCellToExcelCel.get(docTableCell.getCellPos());
                        Cell cell = row.createCell(excelCellPos);
                        cell.setCellStyle(formatRow.getCell(excelCellPos).getCellStyle());
                        cell.setCellValue(docTableCell.getText());
                    }
                }
            }

            cleanUpEmptyRows(sheet, ROW_SHIFT);
            saveWorkBook(workbook, pathToExcelFile);

        } catch (Exception ex) {
            log.error("", ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void deleteRows(String pathToExcelFile, int startRow, int endRow) {
        try (FileInputStream fis = new FileInputStream(pathToExcelFile);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(workbook.getActiveSheetIndex());
            int lastRowNum = endRow;
            if(endRow == -1) {
                lastRowNum = sheet.getLastRowNum();
            }

            for(int rowIndexToDelete = startRow; rowIndexToDelete <= lastRowNum; rowIndexToDelete++ ) {
                Row rowToDelete = sheet.getRow(rowIndexToDelete);
                if (rowToDelete != null) {
                    sheet.removeRow(rowToDelete);
                }
                /*if (rowIndexToDelete >= startRow && rowIndexToDelete < lastRowNum) {
                    sheet.shiftRows(rowIndexToDelete + 1, lastRowNum, -1);
                }*/
            }
            cleanUpEmptyRows(sheet, startRow);
            saveWorkBook(workbook, pathToExcelFile);
        } catch (Exception ex) {
            log.error("", ex);
            throw new RuntimeException(ex);
        }
    }

    private void saveWorkBook(XSSFWorkbook workbook, String pathToExcelFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(pathToExcelFile)) {
            workbook.write(fos);
        }
    }

    private void cleanUpEmptyRows(Sheet sheet, int startRow) {
        for (int i = startRow; i < sheet.getLastRowNum(); i++) {
            Row currentRow = sheet.getRow(i);
            Row nextRow = sheet.getRow(i + 1);

            if (isRowEmpty(currentRow) && isRowEmpty(nextRow)) {
                // Delete the empty row by shifting subsequent rows up
                sheet.shiftRows(i + 1, sheet.getLastRowNum(), -1);
                i--; // Decrement i because the next row now occupies the current index
            }
        }
    }

    private boolean isRowEmpty(Row row)  {
        if(row == null) {
            return true;
        }

        for (int j = 0; j < row.getLastCellNum(); j++) {
            Cell cell = row.getCell(j);
            if (cell != null && cell.getCellType() != CellType.BLANK && !cell.toString().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

}
