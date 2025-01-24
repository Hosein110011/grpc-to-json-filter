package com.example.api_gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "grpc-grpc")
@Data
public class GrpcGrpcConfig {
    private String uri;
    private String path;

}



