package com.w2e.core.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Getter
@Slf4j
public enum W2ESysProp {
    CONFIG_FILE_PATH("w2e.config.path", "cfg/w2e.yml"), DEFAULT_DIR_PATH("user.home", ".");
    private final String sysPropName;
    private final String defaultPath;

    public String getPath() {
        String pathToFile = System.getProperty(getSysPropName(), getDefaultPath());
        log.info("Path to file [{}].", pathToFile);
        return pathToFile;
    }
}
