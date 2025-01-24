package com.example.api_gateway;

import com.example.api_gateway.config.GrpcGrpcConfig;
import com.example.api_gateway.config.GrpcRestConfig;
import com.example.api_gateway.config.RestRestConfig;
import com.example.api_gateway.exception.ExceptionHandler;
import com.example.api_gateway.grpc.MyGrpcServiceGrpc;
import com.example.api_gateway.grpc.TestRequest;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Arrays;

@SpringBootApplication
@EnableConfigurationProperties({GrpcRestConfig.class, GrpcGrpcConfig.class, RestRestConfig.class})
public class ApiGatewayApplication extends MyGrpcServiceGrpc.MyGrpcServiceImplBase{

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}