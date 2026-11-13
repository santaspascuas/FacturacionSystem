package com.facturacionAutonomo.product_service.domain.exception;

// El modificador no está asociado a ese producto.
public class ModifierNoAsignadoException extends RuntimeException{
	public ModifierNoAsignadoException(String message) {
		super(message);
	}

}
