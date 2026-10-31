package com.facturacionAutonomo.customer_service.domain.exception;

public class CustomerNoEncontradoException extends RuntimeException {
	public CustomerNoEncontradoException(String message) {
		super(message);
	}
}
