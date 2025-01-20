package com.example.api_gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


    @ConfigurationProperties(prefix = "grpc-rest")
    @Data
    public class GrpcRestConfig {
        private String uri;
        private String path;

    }



