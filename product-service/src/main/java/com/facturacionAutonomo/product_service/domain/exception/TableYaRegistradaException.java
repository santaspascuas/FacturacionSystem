package com.facturacionAutonomo.product_service.domain.exception;

// El tenant ya tiene una mesa con ese número (responde 409).
public class TableYaRegistradaException extends RuntimeException{
	public TableYaRegistradaException(String message) {
		super(message);
	}

}
