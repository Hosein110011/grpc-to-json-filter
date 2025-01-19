package com.example.api_gateway.config;

import io.grpc.netty.shaded.io.netty.buffer.Unpooled;
import io.grpc.netty.shaded.io.netty.handler.codec.http2.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.ChannelPipelineConfigurer;

@Configuration
public class SpringCloudConfig {

    @Bean
    public NettyServerCustomizer nettyServerCustomizer() {

        return server -> server.doOnConnection((connection -> {
            connection.addHandlerLast(new ChannelHandlerAdapter() {
                @Override
                public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
                    Channel channel = ctx.channel();
                    System.out.println("channel: " + channel);
                    Http2Headers headers = new DefaultHttp2Headers();
                    headers.add("grpc-status", "0");
                    channel.write(new DefaultHttp2HeadersFrame(headers, true))
                            .addListener(future -> {
                                if (future.isSuccess()) {
                                    System.out.println("headers sent successfully!!");
                                } else {
                                    System.err.println("Error sending headers");
                                    future.cause().printStackTrace();
                                }
                            });
                }
            });
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
