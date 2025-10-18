package com.w2e.core.service.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.apache.xmlbeans.ResourceLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Builder
public class ConfigLoaderImpl implements ConfigLoader {
    @Builder.Default
    private final ObjectMapper objectMapper = new ObjectMapper(YAMLFactory.builder().build())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    /**
     * @param pathToConfig Path to configuration file in yaml format
     * @param cls          Class that would hold configuration information
     * @param <T>          Class responsible for configuration
     * @return Class object that holds configuration
     */
    @Override
    public <T> T loadConfiguration(String pathToConfig, Class<T> cls) {
        try {
            // First try to read file from jar or dir relative to where it's executed
            URL pathToConfURL = ResourceLoader.class.getClassLoader().getResource(pathToConfig);
            // Read file by specified path
            if (pathToConfURL == null) {
                pathToConfURL = Paths.get(pathToConfig).toUri().toURL();
            }
            log.debug("\n\nPath to conf URL: [{}] \n\n", pathToConfURL);
            Map<String, Object> mapOfValues = objectMapper.readValue(
                    pathToConfURL
                    , new TypeReference<>() {
                    }
            );
            String ymlContent = readFile(pathToConfURL);
            String content = StringSubstitutor.replace(
                    ymlContent
                    , mapOfValues
                    , "{{"
                    , "}}"
            );
            log.debug("Config file content: {}", content);
            return objectMapper.readValue(content, cls);
        } catch (IOException e) {
            String errMsg = String.format("Exception happened during loading configuration from file [%s]", pathToConfig);
            log.error(errMsg, e);
            throw new RuntimeException(errMsg, e);
        }
    }

    private String readFile(URL fileName) throws IOException {
        try (InputStream is = fileName.openStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            return br.lines().collect(Collectors.joining("\n"));
        }

    }
}
