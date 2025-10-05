package com.w2e.core.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Builder
@Getter
@ToString
public class InputParameter {
    @NonNull
    private String pathToDoc;
    @NonNull
    private  String pathToExcel;

}
