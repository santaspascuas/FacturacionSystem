package com.facturacionAutonomo.product_service.domain.exception;

// Datos de una mesa que no son válidos (responde 400).
public class TableException extends RuntimeException{
	public TableException(String message) {
		super(message);
	}

}
