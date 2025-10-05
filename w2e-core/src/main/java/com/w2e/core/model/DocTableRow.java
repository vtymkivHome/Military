package com.w2e.core.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Builder
@ToString
@Getter
public class DocTableRow implements DocRow {
    private int rowNum;
    List<DocTableCell> cellList;
}
