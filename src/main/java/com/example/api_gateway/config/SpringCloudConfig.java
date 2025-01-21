package com.example.api_gateway.config;

import com.example.api_gateway.interceptor.Http2InterceptorHandler;
import io.grpc.netty.shaded.io.netty.channel.Channel;
import io.grpc.netty.shaded.io.netty.channel.ChannelInitializer;
import io.grpc.netty.shaded.io.netty.handler.codec.http2.*;
import io.netty.buffer.Unpooled;
//import io.netty.channel.*;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.ChannelPipelineConfigurer;

@Configuration
public class SpringCloudConfig {

    private GrpcRestConfig grpcRestConfig;

    @Autowired
    public void setGrpcRestConfig(GrpcRestConfig grpcRestConfig) {
        this.grpcRestConfig = grpcRestConfig;
    }

    @Bean
    public NettyServerCustomizer nettyServerCustomizer() {
        return server -> server.doOnConnection((connection -> {
//            pipeline.addLast(new Http2InterceptorHandler(grpcRestConfig));
            connection.addHandlerLast(new Http2InterceptorHandler(grpcRestConfig));
//                .doOnChannelInit((context, channel, address) -> {
//            Http2Headers headers = new DefaultHttp2Headers();
//            headers.add("grpc-status", "0");
//            channel.writeAndFlush(new DefaultHttp2HeadersFrame(headers, true))
//                    .addListener(future -> {
//                        if (future.isSuccess()) {
//                            System.out.println("headers sent successfully1!!");
//                        } else {
//                            System.err.println("ffffff");
//                            future.cause().printStackTrace();
//                        }
//                    });
//            channel.writeAndFlush(new DefaultHttp2DataFrame(Unpooled.EMPTY_BUFFER, true))
//                    .addListener(future -> {
//                        if (future.isSuccess()) {
//                            System.out.println("stream ended success");
//                        } else {
//                            System.err.println("failed to end stream:");
//                            future.cause().printStackTrace();
//                        }
//                    });
//        });
        }));
    }
}
