package com.w2e.core.service.excel;

import com.w2e.core.config.CoreConfig;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@Builder
public class ExcelServiceImpl implements ExcelService {
    //TODO: This should e. specified in config file
    private CoreConfig config;

    //private final int ROW_SHIFT = 3;
    // Key is document row cell, value is excel row cell
    private Map<Integer, Integer> mapDocCellToExcelCel;

    @Override
    public void writeToExcel(String pathToExcelFile, List<DocTableRow> docTableRowList) {
        XSSFWorkbook workbook;
        try (FileInputStream fis = new FileInputStream(pathToExcelFile)) {
            workbook = new XSSFWorkbook(fis);
        } catch (Exception ex) {
            String errMsg = String.format("Exception happened when reading excel file [%s]", pathToExcelFile);
            log.error(errMsg, ex);
            throw new RuntimeException(errMsg, ex);
        }

        try {
            writeWorkbook(pathToExcelFile, docTableRowList, workbook);
        } catch (IOException e) {
            String errMsg = String.format("Exception happened when when writing to excel file [%s]", pathToExcelFile);
            log.error(errMsg, e);
            throw new RuntimeException(errMsg, e);
        }
    }

    @Override
    public void writeToExcelByTemplate(List<DocTableRow> docTableRowList, String pathToExcelTemplateFile, String pathToExcelOutputFile) {
        XSSFWorkbook workbook;
        // Check if template file exists
        if (!Files.exists(Path.of(pathToExcelTemplateFile))) {
            throw new IllegalArgumentException("Template file for excel does not exist: " + pathToExcelTemplateFile);
        }
        // Read template file
        try (FileInputStream fis = new FileInputStream(pathToExcelTemplateFile)) {
            workbook = new XSSFWorkbook(fis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Check if excel output file exist. If not then create it based on template.
        try {
            writeWorkbook(pathToExcelOutputFile, docTableRowList, workbook);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeWorkbook(String pathToExcelFile, List<DocTableRow> docTableRowList, XSSFWorkbook workbook) throws IOException {
        Sheet sheet = workbook.getSheetAt(workbook.getActiveSheetIndex());
        int shiftRows = config.getExcelDoc().getShiftRows();
        Map<Integer, Integer> docCellToExcelCellMap = config.getDocCellToExcelCellMap();

        int rowNum = sheet.getLastRowNum() + 1;

        if (rowNum < shiftRows) {
            rowNum = shiftRows;
        }

        Row formatRow = sheet.getRow(shiftRows - 1);
        Cell formatCellA = formatRow.getCell(0);

        for (DocTableRow docTableRow : docTableRowList) {
            Row row = sheet.createRow(rowNum++);

            for (DocTableCell docTableCell : docTableRow.getCellList()) {
                if (docCellToExcelCellMap.containsKey(docTableCell.getCellPos())) {
                    Integer excelCellPos = docCellToExcelCellMap.get(docTableCell.getCellPos());
                    Cell cell = row.createCell(excelCellPos);
                    cell.setCellStyle(formatRow.getCell(excelCellPos).getCellStyle());
                    cell.setCellValue(docTableCell.getText());
                }
            }
        }

        autoIncrementCell(sheet, formatCellA, shiftRows);
        cleanUpEmptyRows(sheet, shiftRows);
        saveWorkBook(workbook, pathToExcelFile);
    }

    private void autoIncrementCell(Sheet sheet, Cell formatCellA, int shiftRows) {
        // TODO: Move to config
        int autoIncrementId = 1;
        int autoIncrementCellPos = 0;
        for (int rowPos = shiftRows; rowPos <= sheet.getLastRowNum(); rowPos++) {
            Row row = sheet.getRow(rowPos);
            if (isEmptyRow(row)) {
                continue;
            }
            Cell cell = row.createCell(autoIncrementCellPos, formatCellA.getCellType());
            cell.setCellStyle(formatCellA.getCellStyle());
            cell.setCellValue(autoIncrementId++);
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

            if (isEmptyRow(currentRow) && isEmptyRow(nextRow)) {
                // Delete the empty row by shifting subsequent rows up
                sheet.shiftRows(i + 1, sheet.getLastRowNum(), -1);
                i--; // Decrement i because the next row now occupies the current index
            }
        }
    }

    private boolean isEmptyRow(Row row) {
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
