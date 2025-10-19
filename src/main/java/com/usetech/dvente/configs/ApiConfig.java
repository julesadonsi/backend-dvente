package com.usetech.dvente.configs;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ApiConfig {

    private final Environment env;

    public String getApiUrl() {
        return env.getProperty("server.backend.url");
    }

    public String getDbHost() {
        return env.getProperty("DB_HOST");
    }
}
