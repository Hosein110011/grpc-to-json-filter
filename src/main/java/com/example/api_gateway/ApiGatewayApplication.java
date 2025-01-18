package com.example.api_gateway;

import com.example.api_gateway.exception.ExceptionHandler;
import com.example.api_gateway.grpc.MyGrpcServiceGrpc;
import com.example.api_gateway.grpc.TestRequest;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@SpringBootApplication
public class ApiGatewayApplication extends MyGrpcServiceGrpc.MyGrpcServiceImplBase{

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@Bean
	RouteLocator testRouteLocator(RouteLocatorBuilder routeLocatorBuilder, ExceptionHandler exceptionHandler) {
		return routeLocatorBuilder.routes()
				// ----- Grpc to Rest -----

//				.route("grpc-rest-route", r -> r
//						.path("/example.MyGrpcService/**")
//						.filters(f -> f
//								.removeRequestHeader("Content-Type")
//								.addRequestHeader("Content-Type", "application/json")
//								.modifyRequestBody(DataBuffer.class, String.class, (exchange, body) -> {
//
////									if (exchange.getRequest().getPath().toString().equals("/example.MyGrpcService/testGateway")) {
////										exchange.getRequest().mutate().path("/rest/test").build();
////										System.out.println(exchange.getRequest().getPath());
////									}
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
//								.modifyResponseBody(DataBuffer.class, DataBuffer.class, (exchange, body) -> {
//									System.out.println(body);
//									return Mono.just(body);
//								})
//						)
//						.uri("http://localhost:8088"))

				// ----- Grpc to Grpc -----

				.route("grpc-grpc-route", r -> r
						.path("/**")
						.filters(f -> f.addResponseHeader("X-Request-header", "header-value"))
						.uri("https://localhost:6565")
				)
				.build();
	}

}