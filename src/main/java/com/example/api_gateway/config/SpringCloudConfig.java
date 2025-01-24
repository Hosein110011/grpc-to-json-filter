package com.example.api_gateway.config;

//import io.grpc.netty.shaded.io.netty.channel.Channel;
import com.example.api_gateway.interceptor.CustomHttp2StreamFrameToHttpObjectCodec;
//import io.netty.channel.*;

        import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
        import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringCloudConfig {

    @Bean
    public NettyServerCustomizer nettyServerCustomizer() {
        return server -> server.doOnConnection((connection -> {
            if (connection.channel().pipeline().names().contains("reactor.left.h2ToHttp11Codec")) {
                connection.channel().pipeline().addLast(new CustomHttp2StreamFrameToHttpObjectCodec(true));
            }
            System.out.println("NAMES: " + connection.channel().pipeline().names());
        }));
    }



}
