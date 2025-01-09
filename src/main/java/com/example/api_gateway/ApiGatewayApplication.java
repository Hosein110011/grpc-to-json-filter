package com.example.api_gateway;

import com.example.api_gateway.entity.TestRequestEntity;
import com.example.api_gateway.filter.GrpcToJsonFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@SpringBootApplication
public class ApiGatewayApplication {

	@Autowired
	private GrpcToJsonFilter grpcToJsonFilter;

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@Bean
	RouteLocator testRouteLocator(RouteLocatorBuilder routeLocatorBuilder) {
		return routeLocatorBuilder.routes()
				.route("test-route", r -> r
						.path("/**")
						.filters(f -> f
//								.removeRequestHeader("content-type")
								.addRequestHeader("Content-Type", "application/json")
								.filter(grpcToJsonFilter.apply(grpcToJsonFilter.newConfig())))
						.uri("http://localhost:8088"))
				.build();
	}

}