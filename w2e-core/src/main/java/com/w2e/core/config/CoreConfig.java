package com.w2e.core.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true, allowGetters = true)
public class CoreConfig {
    @JsonProperty("wordDoc")
    private WordDoc wordDoc;

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


}
