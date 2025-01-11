package com.example.api_gateway;

import com.example.api_gateway.entity.TestRequestEntity;
import com.example.api_gateway.filter.GrpcToJsonFilter;
import com.example.api_gateway.grpc.TestRequest;
import com.example.api_gateway.grpc.TestResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.Arrays;

@SpringBootApplication
public class ApiGatewayApplication {

	@Autowired
	private GrpcToJsonFilter grpcToJsonFilter;

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@Bean
	RouteLocator testRouteLocator(RouteLocatorBuilder routeLocatorBuilder) {
		return routeLocatorBuilder.routes()
				.route("test-route", r -> r
						.path("/**")
						.filters(f -> f
								.removeRequestHeader("Content-Type")
								.addRequestHeader("Content-Type", "application/json")
								.modifyRequestBody(DataBuffer.class, String.class, (exchange, body) -> {
									byte[] bytes = new byte[body.readableByteCount()];
									body.read(bytes);
									DataBufferUtils.release(body);

									byte[] actualMessage = Arrays.copyOfRange(bytes, 5, bytes.length);

                                    TestRequest request = null;
                                    try {
                                        request = TestRequest.parseFrom(actualMessage);
                                    } catch (InvalidProtocolBufferException e) {
                                        throw new RuntimeException(e);
                                    }

                                    System.out.println(request);

									String jsonRequest;

                                    try {
                                        jsonRequest = JsonFormat.printer().print(request);
                                    } catch (InvalidProtocolBufferException e) {
                                        throw new RuntimeException(e);
                                    }
                                    System.out.println(jsonRequest);

									return Mono.just(jsonRequest);
								})
								.addResponseHeader("Content-Type", "application/grpc")
								.addResponseHeader("host","localhost:7000")
								.addResponseHeader("x-http2-scheme","http")
								.addResponseHeader("te","trailers")
								.addResponseHeader("user-agent","grpc-java-netty/1.69.0")
								.addResponseHeader("grpc-accept-encoding","gzip")
								.addResponseHeader("x-http2-stream-id","3")
								.modifyResponseBody(String.class, DataBuffer.class, (exchange, body) -> {
									System.out.println("REQ " + exchange.getRequest().getHeaders());
									System.out.println("RES " + exchange.getResponse().getHeaders());
                                    TestResponse response;
									ObjectMapper objectMapper = new ObjectMapper();

									String prettyResp;
                                    try {
										Object jsonObject = objectMapper.readValue(body, Object.class);
                                        prettyResp = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                    System.out.println("ffff : " + prettyResp);
//									byte[] actualByteResponse = Arrays.copyOfRange(body.getBytes(), 5, body.getBytes().length);

                                    try {
										TestResponse.Builder builder = TestResponse.newBuilder();
										JsonFormat.parser().merge(prettyResp, builder);
										TestResponse message = builder.build();
										response = message;
										System.out.println("gGGGG : " + message);
                                    } catch (InvalidProtocolBufferException e) {
                                        throw new RuntimeException(e);
                                    }

                                    byte[] bytes;

                                    bytes = response.toByteArray();
                                    DataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(bytes);
									return Mono.just(dataBuffer);
//									byte[] prefix = new byte[5];
//									prefix[0] = 0;
//									int messageLength = response.toByteArray().length;
//									prefix[1] = (byte) ((messageLength >> 24) & 0xFF);
//									prefix[2] = (byte) ((messageLength >> 16) & 0xFF);
//									prefix[3] = (byte) ((messageLength >> 8) & 0xFF);
//									prefix[4] = (byte) (messageLength & 0xFF);
//
//									byte[] grpcResponse = new byte[prefix.length + response.toByteArray().length];
//									System.arraycopy(prefix, 0, grpcResponse, 0, prefix.length);
//									System.arraycopy(response.toByteArray(), 0, grpcResponse, prefix.length, response.toByteArray().length);
//
//									DataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(grpcResponse);
//									return Mono.just(dataBuffer);
                                }))
//								.filter(grpcToJsonFilter.apply(grpcToJsonFilter.newConfig())))
						.uri("http://localhost:8088"))
				.build();
	}

}