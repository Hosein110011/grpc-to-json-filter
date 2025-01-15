package com.example.api_gateway.config;

//import com.example.api_gateway.filter.GrpcFilter;
//import com.example.api_gateway.filter.GrpcFilter;
import com.example.api_gateway.filter.GrpcHeaderFilter;
import com.example.api_gateway.filter.GrpcToJsonFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.grpc.Channel;
import io.grpc.netty.shaded.io.netty.buffer.Unpooled;
import io.grpc.netty.shaded.io.netty.handler.codec.http2.*;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http2.Http2ConnectionHandler;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.cloud.gateway.filter.headers.GRPCResponseHeadersFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringCloudConfig {

    @Bean
    public GrpcHeaderFilter grpcToJsonFilter() {
        return new GrpcHeaderFilter();
    }

    @Bean
    public GRPCResponseHeadersFilter grpcResponseHeadersFilter() {
        return new GRPCResponseHeadersFilter();
    }

    @Bean
    public NettyServerCustomizer nettyServerCustomizer() {
        return server -> server.doOnChannelInit((context, channel, address) -> {
            Http2Headers headers = new DefaultHttp2Headers();
            headers.add("grpc-status", "0");

            System.out.println("channel id : " + context.currentContext());

            channel.writeAndFlush(new DefaultHttp2HeadersFrame(headers, false))
                    .addListener(future -> {
                        if (future.isSuccess()) {
                            System.out.println("headers sent successfully1!!");
                        } else {
                            System.err.println("ffffff");
                            future.cause().printStackTrace();
                        }
                    });

            channel.writeAndFlush(new DefaultHttp2DataFrame(Unpooled.EMPTY_BUFFER, true))
                    .addListener(future -> {
                        if (future.isSuccess()) {
                            System.out.println("stream ended success");
                        } else {
                            System.err.println("failed to end stream:");
                            future.cause().printStackTrace();
                        }
                    });
        });
    }
}