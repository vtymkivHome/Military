package com.w2e.core.service.parameter;


import com.w2e.core.model.InputParameter;
import com.w2e.core.model.W2ESysProp;
import lombok.Builder;

import java.util.function.Supplier;

import static java.util.Optional.ofNullable;

@Builder
public class ParameterServiceImpl implements ParameterService<InputParameter> {
    public InputParameter loadInputParameter(String[] args, Supplier<String> defaultPathToExcelTemplate) throws ParameterException {
        return parseAndGetInputParameter(args, defaultPathToExcelTemplate);
    }

    private InputParameter parseAndGetInputParameter(String[] args, Supplier<String> defaultPathToExcelTemplate) throws ParameterException {
        String pathToDoc = null;
        String pathToExcel = null;
        String pathToTemplate = null;

        for (String arg : args) {
            if (arg.startsWith("--pathToDoc=")) {
                pathToDoc = arg.substring("--pathToDoc=".length());
            } else if (arg.startsWith("--pathToExcel=")) {
                pathToExcel = arg.substring("--pathToExcel=".length());
            } else if (arg.startsWith("--pathToTemplate=")) {
                pathToTemplate = arg.substring("--pathToTemplate=".length());
            }
        }
        pathToDoc = ofNullable(pathToDoc).orElseThrow(() -> new ParameterException("The parameter --pathToDoc has not been set"));
        pathToExcel = ofNullable(pathToExcel).orElseThrow(() -> new ParameterException("The parameter --pathToExcel has not been set"));

        pathToTemplate = ofNullable(pathToTemplate).orElseGet(defaultPathToExcelTemplate);

        String pathToConfig = W2ESysProp.CONFIG_FILE_PATH.getPath();
        return InputParameter.builder()
                .pathToDoc(pathToDoc)
                .pathToExcel(pathToExcel)
                .pathToTemplate(pathToTemplate)
                .configFilePath(pathToConfig)
                .build();
    }

}
