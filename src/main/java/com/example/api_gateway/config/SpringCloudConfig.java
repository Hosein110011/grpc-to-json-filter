package com.example.api_gateway.config;

import com.example.api_gateway.filter.GrpcToJsonFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.PrintWriter;

@Configuration
public class SpringCloudConfig {

    @Bean
    public GrpcToJsonFilter grpcToJsonFilter() {
        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return new GrpcToJsonFilter(objectWriter);
    }
}