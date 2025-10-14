package com.w2e.core.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true, allowGetters = true)
public class CoreConfig {
    @JsonProperty("excelDoc")
    private ExcelDoc excelDoc;

    @JsonProperty("wordDoc")
    private WordDoc wordDoc;

    @JsonProperty("docCellToExcelCell")
    private List<ExcelDoc.DocCellToExcelCell> docCellToExcelCellList;

    public Map<Integer, Integer> getDocCellToExcelCellMap() {
        return getDocCellToExcelCellList().stream().collect(Collectors.toMap(ExcelDoc.DocCellToExcelCell::getFromCell, ExcelDoc.DocCellToExcelCell::getToCell));
    }

    @Data
    public static class WordDoc {
        @JsonProperty("colData")
        private ColData colData;


        @Data
        public static class ColData {
            @JsonProperty("colPos")
            private int colPos;
            @JsonProperty("colRange")
            private ColRange colRange;
            @JsonProperty("transposed")
            private Transposed transposed;

            @Data
            public static class ColRange {
                @JsonProperty("rowStartPos")
                private int rowStartPos;
                @JsonProperty("rowEndPos")
                private int rowEndPos;
            }

            @Data
            public static class Transposed {
                @JsonProperty("cellStartPosInRow")
                private int cellStartPosInRow;
            }
        }
    }

    @Data
    public static class ExcelDoc {
        @JsonProperty("shiftRows")
        private int shiftRows;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class DocCellToExcelCell {
            @JsonProperty("fromCell")
            private int fromCell;
            @JsonProperty("toCell")
            private int toCell;
        }
    }
}
