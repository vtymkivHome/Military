package com.w2e.core.service.parameter;

public interface ParameterService<T> {
    T loadInputParameter(String[] args) throws ParameterException;
}
