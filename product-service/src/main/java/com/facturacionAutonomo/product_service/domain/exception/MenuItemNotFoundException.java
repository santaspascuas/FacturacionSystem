package com.facturacionAutonomo.product_service.domain.exception;

public class MenuItemNotFoundException extends RuntimeException{
	public MenuItemNotFoundException(String message) {
		super(message);
	}

}
