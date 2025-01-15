package com.example.api_gateway.filter;

import com.example.api_gateway.grpc.TestRequest;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.protobuf.util.JsonFormat;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class GrpcHeaderFilter extends AbstractGatewayFilterFactory<GrpcHeaderFilter.Config>  {

    public GrpcHeaderFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
//        return (ServerWebExchange exchange, GatewayFilterChain chain) -> {
//
//            exchange.getResponse().getHeaders().clearContentHeaders();
//
//            exchange.getResponse().getHeaders().add("Content-Type","application/grpc");
//
//            return chain.filter(exchange).doOnTerminate(() -> {
//                exchange.getResponse().getHeaders().add("grpc-status", "0");
//                chain.filter(exchange);
//            });
//        };
        return (ServerWebExchange exchange, GatewayFilterChain chain) -> {
//            exchange.getResponse().getHeaders().add("grpc-status", "0");
//            exchange.getResponse().setComplete();
            return chain.filter(exchange);
        };
    }

    public static class Config {}

}