package com.example.api_gateway.filter;

import com.example.api_gateway.grpc.TestRequest;
import com.example.api_gateway.grpc.TestResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.grpc.netty.NettyChannelBuilder;
import jdk.jfr.ContentType;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.JsonToGrpcGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.RouteMatcher;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.CACHED_SERVER_HTTP_REQUEST_DECORATOR_ATTR;


public class GrpcToJsonFilter extends AbstractGatewayFilterFactory<GrpcToJsonFilter.Config> {

    private final ObjectWriter objectWriter;

    private final List<HttpMessageReader<?>> messageReaders =
            HandlerStrategies.withDefaults().messageReaders();


    public GrpcToJsonFilter(ObjectWriter objectWriter) {
        super(Config.class);
        this.objectWriter = objectWriter;
    }

    @Override
    public GatewayFilter apply(Config config) {
//        return (ServerWebExchange exchange, GatewayFilterChain chain) -> exchange.getRequest().getBody()
//                .next()
//                .publishOn(Schedulers.boundedElastic())
//                .publishOn(Schedulers.boundedElastic())
//                .publishOn(Schedulers.boundedElastic())
//                .publishOn(Schedulers.boundedElastic())
//                .publishOn(Schedulers.boundedElastic())
//                .publishOn(Schedulers.boundedElastic())
//                .publishOn(Schedulers.boundedElastic())
//                .flatMap(dataBuffer -> {
//
//                    System.out.println(exchange.getRequest().getHeaders());
//
//                    try {
//                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
//                        dataBuffer.read(bytes);
//                        DataBufferUtils.release(dataBuffer);
//
//                        ObjectMapper objectMapper = new ObjectMapper();
//
//                        byte[] actualMessage = Arrays.copyOfRange(bytes, 5, bytes.length);
//
//                        TestRequest request = TestRequest.parseFrom(actualMessage);
//
//                        System.out.println(request);
//
//                        String jsonRequest = JsonFormat.printer().print(request);
//                        System.out.println("json body: " + jsonRequest);
//
//                        TestResponse response = TestResponse.newBuilder().build();
//
//                        RestTemplate restTemplate = new RestTemplate();
//
//                        exchange.getRequest().mutate().header("Content-Type", "application/json");
//
//                        System.out.println(exchange.getRequest().getHeaders());
//
//                        HttpHeaders headers = new HttpHeaders();
//                        headers.setContentType(MediaType.APPLICATION_JSON);
//
//                        System.out.println("llllllll " + headers);
//
//                        HttpEntity<String> requestEntity = new HttpEntity<>(jsonRequest, headers);
//
////                        String responseEntity = restTemplate.postForObject(
////                                "http://localhost:8088/test-grpc/example.MyGrpcService/testGateway",
////                                requestEntity,
////                                String.class
////                        );
////
////                        System.out.println("00000000000000000 " + responseEntity);
//
////                        Mono<byte[]> responseMono = webClient.post()
////                                .uri("/test-grpc/example.MyGrpcService/testGateway")
////                                .contentType(MediaType.APPLICATION_JSON)
////                                .bodyValue(jsonRequest)
////                                .retrieve()
////                                .bodyToMono(byte[].class);
////
////                        responseMono.subscribe(
////                                monoBytes -> {
////                                    try {
////                                        exchange.getResponse().getHeaders().add("Content-Type", "application/grpc");
////                                        System.out.println("dgfrhefrehreuhgj " + monoBytes);
////                                        byte[] actualData = Arrays.copyOfRange(bytes, 5, bytes.length);
////                                        TestResponse testResponse = TestResponse.parseFrom(actualData);
////                                        String jsonResponse = JsonFormat.printer().print(testResponse);
////                                        System.out.println("json resp: " + jsonResponse);
////                                        exchange.getResponse().getHeaders().add("Content-Type", "application/grpc");
////
////                                        String jsonBodyResponse = objectMapper.writeValueAsString(response);
////                                        DataBuffer dataBufferResponse = exchange.getResponse().bufferFactory().wrap(jsonResponse.getBytes(StandardCharsets.UTF_8));
////                                        exchange.getResponse().writeWith(Mono.just(dataBufferResponse));
////                                        exchange.mutate().response(exchange.getResponse()).build();
////
////                                    } catch (Exception e) {
////                                        e.printStackTrace();
////                                    }
////                                },
////                                error -> {
////                                    System.err.println("Error occurred: " + error.getMessage());
////                                }
////                        );
////
////                        String jsonBody = JsonFormat.printer().print(response);
////                        System.out.println("json body: " + jsonBody);
////
////                        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(jsonBody.getBytes(StandardCharsets.UTF_8));
//
//
////                        String json = new String(bytes, StandardCharsets.UTF_8);
////
////                        ObjectMapper objectMapper = new ObjectMapper();
////
////                        TestRequestEntity requestEntity = new TestRequestEntity();
////                        requestEntity.setData(json);
////
////                        String jsonBody = objectMapper.writeValueAsString(requestEntity);
////                        System.out.println("Modified JSON Body: " + jsonBody);
////
////                        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(jsonBody.getBytes(StandardCharsets.UTF_8));
//
//                        exchange.getResponse().getHeaders().add("Content-Type", "application/grpc");
//
////                        String jsonBodyResponse = objectMapper.writeValueAsString(response);
////                        DataBuffer dataBufferResponse = exchange.getResponse().bufferFactory().wrap(responseEntity.getBytes(StandardCharsets.UTF_8));
////                        exchange.getResponse().writeWith(Mono.just(dataBufferResponse));
////                        exchange.mutate().response(exchange.getResponse()).build();
//
////                        URI requestURI = exchange.getRequest().getURI();
////                        NettyChannelBuilder nettyChannelBuilder = NettyChannelBuilder.forAddress(requestURI.getHost(), requestURI.getPort());
////
//
//                        return chain.filter(exchange);
////                                .then(Mono.defer(() -> exchange.getResponse().getBody()
////                                .next()
////                                .flatMap(responseDataBuffer -> {
////                                    try {
////                                        byte[] responseBytes = new byte[responseDataBuffer.readableByteCount()];
////                                        responseDataBuffer.read(responseBytes);
////                                        DataBufferUtils.release(responseDataBuffer);
////
////                                        TestResponse.Builder responseBuilder = TestResponse.newBuilder();
////                                        JsonFormat.parser().merge(new String(responseBytes, StandardCharsets.UTF_8), responseBuilder);
////
////                                        byte[] grpcResponse = responseBuilder.build().toByteArray();
////
////                                        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
////                                        exchange.getResponse().setStatusCode(HttpStatus.OK);
////                                        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(grpcResponse)));
////                                    } catch (Exception e) {
////                                        return Mono.error(e);
////                                    }
////                                })));
//                    } catch (Exception e) {
//                        return Mono.error(e);
//                    }
//                });

        return (exchange, chain) -> ServerWebExchangeUtils
                .cacheRequestBodyAndRequest(exchange, (httpRequest) -> ServerRequest
                        .create(exchange.mutate().request(httpRequest).build(),
                                messageReaders)

                        .bodyToMono(DataBuffer.class)
                        .map(
                            requestPayload -> {
                            byte[] bytes = new byte[requestPayload.readableByteCount()];
                            requestPayload.read(bytes);
                            DataBufferUtils.release(requestPayload);
                            ObjectMapper objectMapper = new ObjectMapper();

                            byte[] actualMessage = Arrays.copyOfRange(bytes, 5, bytes.length);

                                TestRequest request = null;
                                try {
                                    request = TestRequest.parseFrom(actualMessage);
                                } catch (InvalidProtocolBufferException e) {
                                    throw new RuntimeException(e);
                                }

                                String jsonRequest;
                                try {
                                    jsonRequest = JsonFormat.printer().print(request);
                                } catch (InvalidProtocolBufferException e) {
                                    throw new RuntimeException(e);
                                }

                                System.out.println(jsonRequest);

                                return jsonRequest;
                            }
                        )

                        .then(Mono.defer(() -> {
                            ServerHttpRequest cachedRequest = exchange.getAttribute(
                                    CACHED_SERVER_HTTP_REQUEST_DECORATOR_ATTR);
                            cachedRequest = cachedRequest.mutate()
                                    .header("Content-Type", "application/json")
                                    .build();

                            System.out.println(cachedRequest.getBody());

                            return chain.filter(exchange.mutate()
                                    .request(cachedRequest)
                                    .build());
                        })));

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