package com.w2e.core.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.xmlbeans.ResourceLoader;

import java.util.Objects;

@RequiredArgsConstructor
@Getter
public enum W2ESysProp {
    CONFIG_FILE_PATH("w2e.config.path");
    private final String sysPropName;

    public String getPath() {
        return System.getProperty(getSysPropName()
                , Objects.requireNonNull(ResourceLoader.class.getClassLoader().getResource("config/w2e.yml")).getPath());
    }
}
