package com.facturacionAutonomo.product_service.domain.exception;

// La mesa no existe o es de otro tenant (responde 404).
public class TableNotFoundException extends RuntimeException{
	public TableNotFoundException(String message) {
		super(message);
	}

}
