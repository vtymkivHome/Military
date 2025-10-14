package com.w2e.core.service.config;

public interface ConfigLoader {
    <T> T loadConfiguration(final String pathToConfig, Class<T> cls);
}
