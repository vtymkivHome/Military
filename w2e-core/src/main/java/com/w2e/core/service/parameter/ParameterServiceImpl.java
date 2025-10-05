package com.w2e.core.service.parameter;


import com.w2e.core.model.InputParameter;
import lombok.Builder;

import static java.util.Optional.ofNullable;

@Builder
public class ParameterServiceImpl implements ParameterService<InputParameter> {
    public InputParameter loadInputParameter(String[] args) throws ParameterException {
        return parseAndGetInputParameter(args);
    }

    private InputParameter parseAndGetInputParameter(String[] args) throws ParameterException {
        String pathToDoc = null;
        String pathToExcel = null;

        for (String arg : args) {
            if (arg.startsWith("--pathToDoc=")) {
                pathToDoc = arg.substring("--pathToDoc=".length());
            } else if (arg.startsWith("--pathToExcel=")) {
                pathToExcel = arg.substring("--pathToExcel=".length());
            }
        }
        pathToDoc = ofNullable(pathToDoc).orElseThrow(() -> new ParameterException("The parameter --pathToDoc has not been set"));
        pathToExcel = ofNullable(pathToExcel).orElseThrow(() -> new ParameterException("The parameter --pathToExcel has not been set"));
        return InputParameter.builder()
                .pathToDoc(pathToDoc)
                .pathToExcel(pathToExcel)
                .build();
    }

}
