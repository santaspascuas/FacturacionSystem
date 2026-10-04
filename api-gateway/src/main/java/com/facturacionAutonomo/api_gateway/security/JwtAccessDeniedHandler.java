package com.facturacionAutonomo.api_gateway.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class JwtAccessDeniedHandler implements ServerAccessDeniedHandler{
	
	private final ErrorResponseWriter writer;
	

	public JwtAccessDeniedHandler(ErrorResponseWriter writer) {
		super();
		this.writer = writer;
	}

	@Override
	public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
		// TODO Auto-generated method stub
		return writer.write(exchange,HttpStatus.FORBIDDEN, "No tienes permisos para este recurso");
	}

}
