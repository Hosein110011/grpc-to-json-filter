package com.example.api_gateway;

import com.example.api_gateway.config.GrpcRestConfig;
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
@EnableConfigurationProperties({GrpcRestConfig.class})
public class ApiGatewayApplication extends MyGrpcServiceGrpc.MyGrpcServiceImplBase{

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@Bean
	RouteLocator testRouteLocator(RouteLocatorBuilder routeLocatorBuilder, ExceptionHandler exceptionHandler) {
		return routeLocatorBuilder.routes()
				// ----- Grpc to Rest -----

//				.route("grpc-rest-route", r -> r
//						.path("/example.MyGrpcService/testGateway")
//						.filters(f -> f
//								.removeRequestHeader("Content-Type")
//								.setPath("/rest/test")
//								.addRequestHeader("Content-Type", "application/json")
//								.modifyRequestBody(DataBuffer.class, String.class, (exchange, body) -> {
//
//									byte[] bytes = new byte[body.readableByteCount()];
//									body.read(bytes);
//									DataBufferUtils.release(body);
//
//									byte[] actualMessage = Arrays.copyOfRange(bytes, 5, bytes.length);
//
//                                    TestRequest request = null;
//                                    try {
//                                        request = TestRequest.parseFrom(actualMessage);
//                                    } catch (InvalidProtocolBufferException e) {
//                                        throw new RuntimeException(e);
//                                    }
//
//									String jsonRequest;
//
//                                    try {
//                                        jsonRequest = JsonFormat.printer().print(request);
//                                    } catch (InvalidProtocolBufferException e) {
//                                        throw new RuntimeException(e);
//                                    }
//
//									return Mono.just(jsonRequest);
//								})
//								.addResponseHeader("Content-Type", "application/grpc")
////								.modifyResponseBody(DataBuffer.class, DataBuffer.class, (exchange, body) -> {
////                                    return Mono.just(body);
////								})
//						)
//						.uri("http://localhost:8088"))

				// ----- Grpc to Grpc -----

				.route("grpc-grpc-route", r -> r
						.path("/example.MyGrpcService/testGrpcGateway")
						.filters(f -> f.addResponseHeader("X-Request-header", "header-value"))
						.uri("https://localhost:6565")
				)
				.build();
	}

}