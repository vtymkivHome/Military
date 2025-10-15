package com.w2e.core.config;

import com.w2e.core.model.W2ESysProp;
import com.w2e.core.service.config.ConfigLoaderImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CoreConfigTest {
    String pathToConfig = W2ESysProp.CONFIG_FILE_PATH.getDefaultPath();
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