package com.example.api_gateway.filter;

import com.example.api_gateway.grpc.TestRequest;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.protobuf.util.JsonFormat;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class GrpcHeaderFilter extends AbstractGatewayFilterFactory<GrpcHeaderFilter.Config> implements Ordered {

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

            return chain.filter(exchange).doFinally(signalType -> {
                exchange.getResponse().setComplete();
                exchange.getResponse().getHeaders().add("grpc-status", "0");
                System.out.println("Final operation, signal type: " + signalType);
            });
        };
    }

    public static class Config {}

    @Override
    public int getOrder() {
        return -1;
    }

}