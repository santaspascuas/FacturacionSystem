package com.facturacionAutonomo.product_service.domain.exception;

// Datos de un modificador, o de su asociación a un producto, que no son válidos.
public class ModifierException extends RuntimeException{
	public ModifierException(String message) {
		super(message);
	}

}
