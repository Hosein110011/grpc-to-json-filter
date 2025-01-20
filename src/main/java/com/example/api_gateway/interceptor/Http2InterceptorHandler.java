package com.example.api_gateway.interceptor;

import com.example.api_gateway.config.GrpcRestConfig;
import com.example.api_gateway.grpc.TestRequest;
import com.example.api_gateway.grpc.TestResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.grpc.netty.shaded.io.netty.buffer.ByteBuf;
import io.grpc.netty.shaded.io.netty.buffer.Unpooled;
import io.grpc.netty.shaded.io.netty.handler.codec.http.HttpResponseStatus;
import io.grpc.netty.shaded.io.netty.handler.codec.http2.*;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import jdk.jfr.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@Component
public class Http2InterceptorHandler extends SimpleChannelInboundHandler {
    private DefaultHttpRequest request;
    private String jsonRequestBody;
    private RestTemplate restTemplate = new RestTemplate();
    GrpcRestConfig grpcRestConfig;

    public Http2InterceptorHandler(GrpcRestConfig grpcRestConfig) {
        this.grpcRestConfig = grpcRestConfig;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof DefaultHttpRequest) {
            this.request = (DefaultHttpRequest) msg;

            if (request.uri().equals(grpcRestConfig.getPath())) {
                request.setUri(grpcRestConfig.getUri() + grpcRestConfig.getPath());
                System.out.println(request.uri());
            }
        }

        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;

            ByteBuffer buf = ByteBuffer.allocate(httpContent.content().readableBytes());

            httpContent.content().forEachByte(bt -> {
                buf.put(bt);
                return true;
            });

            byte[] actualMessage = Arrays.copyOfRange(buf.array(), 5, buf.array().length);

            TestRequest grpcRequest = null;
            try {
                grpcRequest = TestRequest.parseFrom(actualMessage);
            } catch (InvalidProtocolBufferException e) {
                throw new RuntimeException(e);
            }

            String jsonRequest;

            try {
                jsonRequest = JsonFormat.printer().print(grpcRequest);
            } catch (InvalidProtocolBufferException e) {
                throw new RuntimeException(e);
            }

            this.jsonRequestBody = jsonRequest;

            HttpHeaders httpHeaders = new DefaultHttpHeaders();

            httpHeaders.add("ContentType", MediaType.APPLICATION_JSON);

//            HttpEntity <TestRequest> entity = new HttpEntity<TestRequest>(grpcRequest, httpHeaders);
//
//            restTemplate.exchange(request.uri(), HttpMethod.POST, entity, TestResponse.class);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}