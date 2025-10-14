package com.w2e.core.service.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Paths;

@Slf4j
public class ConfigLoaderImpl implements ConfigLoader {
    private final ObjectMapper objectMapper;

    public ConfigLoaderImpl(ObjectMapper objectMapper) {
        // Excludes feature FAIL_ON_UNKNOWN_PROPERTIES
        this.objectMapper = objectMapper;
    }

    public ConfigLoaderImpl() {
        // Excludes feature FAIL_ON_UNKNOWN_PROPERTIES
        this.objectMapper = new ObjectMapper(YAMLFactory.builder().build())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * @param pathToConfig Path to configuration file in yaml format
     * @param cls          Class that would hold configuration information
     * @param <T>          Class responsible for configuration
     * @return Class object that holds configuration
     */
    @Override
    public <T> T loadConfiguration(String pathToConfig, Class<T> cls) {
        try {
            return objectMapper.readValue(Paths.get(pathToConfig).toFile(), cls);
        } catch (IOException e) {
            log.error("Exception happened during loading configuration {}", pathToConfig, e);
            throw new RuntimeException(e);
        }
    }
}
