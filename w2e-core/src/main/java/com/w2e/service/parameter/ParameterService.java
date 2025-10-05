package com.w2e.service.parameter;

public interface ParameterService<T> {
    T loadInputParameter(String[] args) throws ParameterException;
}
