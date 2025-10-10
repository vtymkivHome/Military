package com.w2e.core.service.parameter;

import java.util.function.Supplier;

public interface ParameterService<T> {
    T loadInputParameter(String[] args, Supplier<String> defaultPathToExcelTemplate) throws ParameterException;
}
