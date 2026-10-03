package com.facturacionAutonomo.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutingConfig {
	
	
	@Bean
	public RouteLocator router(RouteLocatorBuilder builder) {
		
		return builder.routes().route("demo", r->r.path("/demo/**").filters(f->f.stripPrefix(1)).uri("http:/localhost:8080")).build();
		
	}
	
	
	

}
