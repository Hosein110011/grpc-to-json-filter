package com.example.api_gateway;

//import com.example.api_gateway.filter.GrpcFilter;
import com.example.api_gateway.exception.ExceptionHandler;
import com.example.api_gateway.filter.GrpcHeaderFilter;
import com.example.api_gateway.grpc.GrpcServer;
import com.example.api_gateway.grpc.MyGrpcServiceGrpc;
import com.example.api_gateway.grpc.TestRequest;
import com.example.api_gateway.grpc.TestResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.grpc.BindableService;
import io.grpc.Metadata;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.headers.GRPCResponseHeadersFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.util.*;

import static reactor.core.publisher.SignalType.ON_COMPLETE;

@SpringBootApplication
public class ApiGatewayApplication extends MyGrpcServiceGrpc.MyGrpcServiceImplBase{

	@Autowired
	private GrpcHeaderFilter grpcHeaderFilter;

	@Autowired
	GrpcServer grpcServer;

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@Bean
	RouteLocator testRouteLocator(RouteLocatorBuilder routeLocatorBuilder, ExceptionHandler exceptionHandler) {
		Metadata.Key<String> myHeaderKey = Metadata.Key.of("grpc-status", Metadata.ASCII_STRING_MARSHALLER);

		Metadata metadata = new Metadata();

		metadata.put(myHeaderKey, "0");
//		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9091)
//				.usePlaintext()
//				.build();
//
//		MyGrpcServiceGrpc.MyGrpcServiceBlockingStub blockingStub = MyGrpcServiceGrpc.newBlockingStub(channel);
		return routeLocatorBuilder.routes()
				.route("test-route", r -> r
						.path("/**")
//						.filters(f -> f
//								.addResponseHeader("X-Request-header", "header-value")
//								.modifyRequestBody(DataBuffer.class, DataBuffer.class, (exchange, body) -> {
//									byte[] bytes = new byte[body.readableByteCount()];
//									body.read(bytes);
//									DataBufferUtils.release(body);
//
//									System.out.println(exchange.getRequest().getURI().toString());
//
//
//									System.out.println("Akhare khat " + Arrays.toString(bytes));
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
//									System.out.println("ReQ: " + request);
//
//
//									TestResponse response = blockingStub.testGateway(request);
////
//									System.out.println("edrf " + response);
//
//									byte[] responseBytes;
//
//
////
//                                    responseBytes = response.toByteArray();
//
//									System.out.println("Avale khat " + Arrays.toString(responseBytes));
//
//									System.out.println(responseBytes.length);
//
//									byte[] anotherBytes = new byte[] {0,0,0,0,(byte)responseBytes.length};
////
//									byte[] newByte = concatWithArrayCopy(anotherBytes, bytes);
//
//									System.out.println("ZZZZZZZZZZz " + Arrays.toString(responseBytes));
//
//
//
//
//									exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(responseBytes)));
//
//
//
//									return Mono.just(body);
//								}))
						.filters(f -> f
								.removeRequestHeader("Content-Type")
								.addRequestHeader("Content-Type", "application/json")
								.modifyRequestBody(DataBuffer.class, String.class, (exchange, body) -> {

//									exchange.getRequest().getHeaders().replace("Content-Type", Collections.singletonList("application/grpc"), Collections.singletonList("application/json"));
									byte[] bytes = new byte[body.readableByteCount()];
									body.read(bytes);
									DataBufferUtils.release(body);

									System.out.println(exchange.getRequest().getURI());

									System.out.println("Akhare khat " + Arrays.toString(bytes));

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

//									exchange.getResponse().getHeaders().set("Content-Type", "application/grpc");
//									exchange.getResponse().getHeaders().set("grpc-message", "100");



//                                    exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(Arrays.copyOfRange(bytes, 0, 5))));

									return Mono.just(jsonRequest);
								})
								.removeResponseHeader("Date")
//								.removeResponseHeader("Content-Type")
//								.addResponseHeader("content-type", "application/proto")
//								.addResponseHeader("Content-Type", "application/grpc")
//								.addResponseHeader("grpc-message","100")
//								.addResponseHeader("x-http2-scheme","http")
//								.addResponseHeader("te","trailers")
//								.addResponseHeader("user-agent","grpc-java-netty/1.69.0")
//								.addResponseHeader("grpc-accept-encoding","gzip")
//								.addResponseHeader("x-http2-stream-id","3")
//								.addResponseHeader("grpc-status", "0")
								.modifyResponseBody(String.class, DataBuffer.class, (exchange, body) -> {
									System.out.println("REQ " + exchange.getRequest().getHeaders());
									System.out.println("RES " + exchange.getResponse().getHeaders());



                                    TestResponse response;
									ObjectMapper objectMapper = new ObjectMapper();

									System.out.println("sout get remote address " + exchange.getRequest().getRemoteAddress());

									var grpcService = NettyServerBuilder.forAddress(exchange.getRequest().getRemoteAddress()).addService((BindableService) grpcServer).build();

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

									System.out.println("Avale khat " + Arrays.toString(bytes));

									System.out.println(bytes.length);

									int payloadSize = bytes.length;
									ByteBuffer buf = ByteBuffer.allocate(payloadSize + 5);
									buf.put((byte) 0);
									buf.putInt(payloadSize);
									buf.put(bytes);

//									byte[] anotherBytes = new byte[] {0,0,0,0,35};
//
//									byte[] newByte = concatWithArrayCopy(anotherBytes, bytes);

									System.out.println("ZZZZZZZZZZz " + Arrays.toString(buf.array()));




//									exchange.getResponse().beforeCommit(() -> {
////										System.out.println("neutral " + exchange.getResponse().getHeaders());
//
//
//										System.out.println("brfore headers: " + exchange.getResponse().getHeaders());
//
//
									GRPCResponseHeadersFilter responseHeadersFilter = new GRPCResponseHeadersFilter();

									System.out.println("H2C: " + exchange.getResponse().getHeaders());
//
//
									exchange.getResponse().getHeaders().clearContentHeaders();
//
            						exchange.getResponse().getHeaders().add("Content-Type","application/grpc");
//
									HttpHeaders headers = responseHeadersFilter.filter(exchange.getResponse().getHeaders(), exchange);
//
									exchange.getResponse().getHeaders().putAll(headers);
//
//										exchange.getResponse().getHeaders().add("grpc-encoding", "identity");
//										exchange.getResponse().getHeaders().add("grpc-accept-encoding", "gzip");
//										exchange.getResponse().getHeaders().add("status", "200");
//
//										exchange.getResponse().getHeaders().add("grpc-status","0");
//										exchange.getResponse().getHeaders().add("grpc-message","OK");
//
										System.out.println("after headers: " + exchange.getResponse().getHeaders());
//
//										return Mono.empty();
//									});


									DataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(buf.array());

									exchange.getResponse().beforeCommit(() -> {
										exchange.getResponse().getHeaders().add("X-EndStream-Control", "false");
										return Mono.empty();
									});

									return Mono.just(dataBuffer);

////									byte[] prefix = new byte[5];
////									prefix[0] = 0;
////									int messageLength = response.toByteArray().length;
////									prefix[1] = (byte) ((messageLength >> 24) & 0xFF);
////									prefix[2] = (byte) ((messageLength >> 16) & 0xFF);
////									prefix[3] = (byte) ((messageLength >> 8) & 0xFF);
////									prefix[4] = (byte) (messageLength & 0xFF);
////
////									byte[] grpcResponse = new byte[prefix.length + response.toByteArray().length];
////									System.arraycopy(prefix, 0, grpcResponse, 0, prefix.length);
////									System.arraycopy(response.toByteArray(), 0, grpcResponse, prefix.length, response.toByteArray().length);
////
////									DataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(grpcResponse);
////									return Mono.just(dataBuffer);

                                })
//										.addResponseHeader("content-type", "application/grpc")
//										.filter(grpcHeaderFilter.apply(grpcHeaderFilter.newConfig()))
//								.removeResponseHeader("Content-Length")
						)
////								.filter(grpcToJsonFilter.apply(grpcToJsonFilter.newConfig())))\
						.uri("http://localhost:8088"))
				.build();
	}

	private <T> byte[] concatWithArrayCopy(byte[] array1, byte[] array2) {
		byte[] result = Arrays.copyOf(array1, array1.length + array2.length);
		System.arraycopy(array2, 0, result, array1.length, array2.length);
		return result;
	}

}