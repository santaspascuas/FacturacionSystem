package com.facturacionAutonomo.api_gateway.security;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;

@Component
public class ErrorResponseWriter {
	
	
	private final JsonMapper jsonMapper;

	public ErrorResponseWriter(JsonMapper jsonMapper) {
		super();
		this.jsonMapper = jsonMapper;
	}
	
	public Mono<Void> write(ServerWebExchange exchange, HttpStatus unauthorized, String message) {
		
		ServerHttpResponse response     =  exchange.getResponse();
		
		response.setStatusCode(unauthorized);
		response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
		
		ErrorResponse errorbody = ErrorResponse.crearError(unauthorized, message, exchange.getRequest().getPath().value());
		//usamos los buffer porque es lo que se usa en las cadenas de envio de mensaje dentre señal
        DataBuffer buffer = response.bufferFactory().wrap(jsonMapper.writeValueAsBytes(errorbody));
        return response.writeWith(Mono.just(buffer));
	}

}
