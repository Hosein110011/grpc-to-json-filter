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
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
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

//    @Bean
//    public NettyServerCustomizer nettyServerCustomizer() {
//        return server -> {
//            Http2Connection connection = new DefaultHttp2Connection(true);
//            Http2ConnectionDecoder decoder = new DefaultHttp2ConnectionDecoder(connection, new Http2FrameListenerAdapter());
//            Http2ConnectionEncoder encoder = new DefaultHttp2ConnectionEncoder(connection, new DefaultHttp2FrameWriter());
//            Http2Settings settings = new Http2Settings();
//
//            Http2FrameCodec codec = new Http2FrameCodec(decoder, encoder, settings, true, false);
//            server.pipeline().addLast("http2Codec", codec);
//            return server;
//        };
//    }
}