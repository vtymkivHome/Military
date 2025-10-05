package com.w2e.core.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@ToString
@Getter
public class DocTableCell implements DataCell {
   int cellPos;
   String text;
}
