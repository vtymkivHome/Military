package com.w2e.core.config;

import com.w2e.core.service.config.ConfigLoaderImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CoreConfigTest {
    String pathToConfig = "src/test/resources/config/w2e.yml";
    private CoreConfig coreConfig;

    @BeforeEach
    void setUp() {
        coreConfig = ConfigLoaderImpl.builder().build().loadConfiguration(pathToConfig, CoreConfig.class);
    }

    @Test
    void getWordDoc() {
        assertNotNull(coreConfig.getWordDoc());
    }
}