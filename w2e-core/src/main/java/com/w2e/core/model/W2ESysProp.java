package com.w2e.core.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Getter
@Slf4j
public enum W2ESysProp {
    CONFIG_FILE_PATH("w2e.config.path", "cfg/w2e.yml");
    private final String sysPropName;
    private final String defaultPath;

    public String getPath() {
        String pathToConfigFile = System.getProperty(getSysPropName(), getDefaultPath());
        log.info("Path to config file [{}].", pathToConfigFile);
        return pathToConfigFile;
    }
}
