package com.facturacionAutonomo.product_service.domain.exception;

/**
 * El tenant ya tiene una categoría con ese nombre. El handler la convierte en un 409.
 */
public class CategoryYaRegistradoException extends RuntimeException{

	public CategoryYaRegistradoException(String message) {
		super(message);
	}

}
