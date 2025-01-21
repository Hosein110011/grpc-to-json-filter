package com.example.api_gateway.interceptor;

import com.example.api_gateway.config.GrpcRestConfig;
import com.example.api_gateway.grpc.TestRequest;
import com.example.api_gateway.grpc.TestResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.grpc.netty.shaded.io.netty.buffer.ByteBuf;
import io.grpc.netty.shaded.io.netty.buffer.Unpooled;
import io.grpc.netty.shaded.io.netty.handler.codec.http2.*;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutor;
import jdk.jfr.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.Http2;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.reactive.HttpHeadResponseDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Future;


import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.handler.ssl.ApplicationProtocolNames.HTTP_2;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

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
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//        ctx.flush();
        super.channelReadComplete(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof DefaultHttpRequest) {
            this.request = (DefaultHttpRequest) msg;

            if (request.uri().equals(grpcRestConfig.getPath())) {
                request.setUri(grpcRestConfig.getUri() + "/rest/test");
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

            TestRequest grpcRequest;
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

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonRequestBody, headers);

            String responseEntity = restTemplate.postForObject(
                    request.uri(),
                    requestEntity,
                    String.class
            );

            System.out.println("response : " + responseEntity);

            byte[] bytesResponse = getStructuredGrpcResponse(handleResponseBody(responseEntity)).array();

            System.out.println("structured resp " + Arrays.toString(bytesResponse));

            if (msg instanceof LastHttpContent) {
                LastHttpContent lastHttpContent = (LastHttpContent) msg;
                writeResponse(ctx, lastHttpContent, bytesResponse);
            }

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        cause.printStackTrace();
//        ctx.close();
        super.exceptionCaught(ctx, cause);
    }

    private TestResponse handleResponseBody(String jsonResponseBody) {
        TestResponse response;
        try {
            TestResponse.Builder builder = TestResponse.newBuilder();
            JsonFormat.parser().merge(jsonResponseBody, builder);
            response = builder.build();
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }

        System.out.println("grpc response : " + response);

        return response;

    }

    private ByteBuffer getStructuredGrpcResponse(TestResponse response) {
        byte[] byteResponse = response.toByteArray();

        int payloadSize = byteResponse.length;
        ByteBuffer buf = ByteBuffer.allocate(payloadSize + 5);
        buf.put((byte) 0);
        buf.putInt(payloadSize);
        buf.put(byteResponse);

        return buf;
    }

    private void writeResponse(ChannelHandlerContext ctx, LastHttpContent trailer, byte[] responseData) {
        boolean keepAlive = HttpUtil.isKeepAlive(request);

        FullHttpResponse httpResponse = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK);

        httpResponse.content().writeBytes(responseData);

        httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/grpc");
        httpResponse.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
        httpResponse.headers().set(HttpHeaderNames.TRAILER, "grpc-status");
        httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);

        Http2Headers http2Headers = new DefaultHttp2Headers();
        http2Headers.add(HttpHeaderNames.CONTENT_TYPE, "application/grpc");
        Http2HeadersFrame msgHeader = new DefaultHttp2HeadersFrame(http2Headers);
        System.out.println("keep alive " + keepAlive);
        if (keepAlive) {
            System.out.println("end stream: " + msgHeader.isEndStream());

//            ByteBuf content = Unpooled.copiedBuffer(ctx.alloc().buffer().array());
//            content.writeBytes(Unpooled.EMPTY_BUFFER);

//            Http2Headers headers = new DefaultHttp2Headers().status(HttpResponseStatus.OK.codeAsText());
//            Http2Headers endHeaders = new DefaultHttp2Headers().status(HttpResponseStatus.OK.codeAsText());
//            Http2HeadersFrame endMsg = new DefaultHttp2HeadersFrame(endHeaders);
//            endHeaders.add("grpc-status", "0");
//            ctx.write(new DefaultHttp2HeadersFrame(headers).stream(msgHeader.stream()));
//            ctx.write(new DefaultHttp2DataFrame(Unpooled.EMPTY_BUFFER, false).stream(msgHeader.stream()));
//            ctx.write(new DefaultHttp2HeadersFrame(endHeaders).stream(endMsg.stream()));
//            ctx.write(new DefaultHttp2DataFrame(Unpooled.EMPTY_BUFFER, true).stream(msgHeader.stream()));
//
//
//
//
            FullHttpResponse httpEOSResponse = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK);
            httpEOSResponse.headers().set("grpc-status", "0");
            httpEOSResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/grpc");
            httpEOSResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);


//            ChannelHandlerContext context = new ChannelHandlerContextDecorator(ctx);

            Flux.just(httpResponse, httpEOSResponse)
                    .doOnNext(response -> ctx.writeAndFlush(response))
                    .doOnComplete(ctx::flush)
                    .subscribe();
//
////            ctx.write(httpEOSResponse);
//            ctx.flush();

//            Http2Headers responseHeaders = new DefaultHttp2Headers()
//                    .status(HttpResponseStatus.OK.codeAsText())
//                    .add(HttpHeaderNames.CONTENT_TYPE, "application/grpc");
//            ctx.write(new DefaultHttp2HeadersFrame(responseHeaders, false));
//
//            ByteBuf content = Unpooled.wrappedBuffer(responseData);
//            ctx.write(new DefaultHttp2DataFrame(content, false));
//
//            Http2Headers trailers = new DefaultHttp2Headers()
//                    .add("grpc-status", "0")
//                    .add("grpc-message", "OK");
//            ctx.write(new DefaultHttp2HeadersFrame(trailers, true));
//
//            ctx.flush();
        }

        if (!keepAlive) {
            System.out.println("is keep alive " + keepAlive);
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        } else {
            ctx.flush();
        }
    }
}