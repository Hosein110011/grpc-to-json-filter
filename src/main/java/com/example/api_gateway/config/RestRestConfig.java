package com.example.api_gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rest-rest")
@Data
public class RestRestConfig {
    private String uri;
    private String path;

}
