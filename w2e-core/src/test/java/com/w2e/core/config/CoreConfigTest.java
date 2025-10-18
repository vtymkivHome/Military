package com.w2e.core.config;

import com.w2e.core.model.W2ESysProp;
import com.w2e.core.service.config.ConfigLoaderImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class CoreConfigTest {
    String pathToConfig = W2ESysProp.CONFIG_FILE_PATH.getDefaultPath();

    @Test
    void getWordDoc() {
        CoreConfig coreConfig = ConfigLoaderImpl.builder().build().loadConfiguration(pathToConfig, CoreConfig.class);
        assertNotNull(coreConfig.getWordDoc());
    }
}