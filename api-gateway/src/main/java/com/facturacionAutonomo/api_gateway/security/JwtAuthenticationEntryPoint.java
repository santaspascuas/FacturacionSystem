package com.facturacionAutonomo.api_gateway.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationEntryPoint implements ServerAuthenticationEntryPoint{
	
	private final ErrorResponseWriter writer;
	

	public JwtAuthenticationEntryPoint(ErrorResponseWriter writer) {
		super();
		this.writer = writer;
	}


	@Override
	public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
		String message = (ex instanceof InvalidBearerTokenException)?"Token invalido o caducado": "Se requiere autenticacion";
		
		exchange.getResponse().getHeaders().set("WWW-Authenticate", "Bearer");
		
		return writer.write(exchange, HttpStatus.UNAUTHORIZED, message);
	}
	
	

}
