package com.facturacionAutonomo.product_service.domain.exception;

// Datos de un pedido o de una línea que no son válidos, o una operación que el pedido no admite (responde 400).
public class OrderException extends RuntimeException{
	public OrderException(String message) {
		super(message);
	}

}
