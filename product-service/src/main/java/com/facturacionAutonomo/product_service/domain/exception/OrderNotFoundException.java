package com.facturacionAutonomo.product_service.domain.exception;

// El pedido no existe o es de otro tenant (responde 404).
public class OrderNotFoundException extends RuntimeException{
	public OrderNotFoundException(String message) {
		super(message);
	}

}
