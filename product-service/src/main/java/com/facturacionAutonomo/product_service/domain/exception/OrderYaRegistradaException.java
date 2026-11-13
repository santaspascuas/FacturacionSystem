package com.facturacionAutonomo.product_service.domain.exception;

// El número de pedido o la clave de idempotencia ya existen en el tenant (responde 409).
// Pasa si dos pedidos se registran a la vez: basta con reintentar.
public class OrderYaRegistradaException extends RuntimeException{
	public OrderYaRegistradaException(String message) {
		super(message);
	}

}
