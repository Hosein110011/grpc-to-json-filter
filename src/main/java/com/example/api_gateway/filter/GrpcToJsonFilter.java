package com.example.api_gateway.filter;

import com.example.api_gateway.entity.TestRequestEntity;
import com.example.api_gateway.grpc.TestRequest;
import com.example.api_gateway.grpc.TestResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.util.JsonFormat;
import io.grpc.stub.ClientCalls;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Function;

public class GrpcToJsonFilter extends AbstractGatewayFilterFactory<GrpcToJsonFilter.Config> {

    private final ObjectWriter objectWriter;

    public GrpcToJsonFilter(ObjectWriter objectWriter) {
        super(Config.class);
        this.objectWriter = objectWriter;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (ServerWebExchange exchange, GatewayFilterChain chain) -> exchange.getRequest().getBody()
                .next()
                .flatMap(dataBuffer -> {

                    System.out.println(exchange.getRequest().getHeaders());

                    try {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        DataBufferUtils.release(dataBuffer);

                        byte[] actualMessage = Arrays.copyOfRange(bytes, 5, bytes.length);

                        TestRequest grpcMessage = TestRequest.parseFrom(actualMessage);

                        String jsonBody = JsonFormat.printer().print(grpcMessage);
                        System.out.println("json body: " + jsonBody);

                        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(jsonBody.getBytes(StandardCharsets.UTF_8));


//                        String json = new String(bytes, StandardCharsets.UTF_8);
//
//                        ObjectMapper objectMapper = new ObjectMapper();
//
//                        TestRequestEntity requestEntity = new TestRequestEntity();
//                        requestEntity.setData(json);
//
//                        String jsonBody = objectMapper.writeValueAsString(requestEntity);
//                        System.out.println("Modified JSON Body: " + jsonBody);
//
//                        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(jsonBody.getBytes(StandardCharsets.UTF_8));

                        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                                .header("Content-Type", "application/json")
                                .build();

                        ServerWebExchange modifiedExchange = exchange.mutate()
                                .request(new ServerHttpRequestDecorator(modifiedRequest) {
                                    @Override
                                    public Flux<DataBuffer> getBody() {
                                        return Flux.just(buffer);
                                    }
                                }).build();

                        return chain.filter(modifiedExchange);
//                                .then(Mono.defer(() -> exchange.getResponse().getBody()
//                                .next()
//                                .flatMap(responseDataBuffer -> {
//                                    try {
//                                        byte[] responseBytes = new byte[responseDataBuffer.readableByteCount()];
//                                        responseDataBuffer.read(responseBytes);
//                                        DataBufferUtils.release(responseDataBuffer);
//
//                                        TestResponse.Builder responseBuilder = TestResponse.newBuilder();
//                                        JsonFormat.parser().merge(new String(responseBytes, StandardCharsets.UTF_8), responseBuilder);
//
//                                        byte[] grpcResponse = responseBuilder.build().toByteArray();
//
//                                        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
//                                        exchange.getResponse().setStatusCode(HttpStatus.OK);
//                                        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(grpcResponse)));
//                                    } catch (Exception e) {
//                                        return Mono.error(e);
//                                    }
//                                })));
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                });
    }

    public static class Config {
    }

    private Function<JsonNode, byte[]> callGRPCServer() {
        return (jsonResponse) -> {
            try {
                this.objectWriter.writeValueAsBytes(jsonResponse);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return new byte[0];
        };
    }

}