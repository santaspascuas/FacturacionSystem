package com.facturacionAutonomo.product_service.domain.exception;

// El modificador ya está asociado a ese producto (UNIQUE de menu_item_modifiers).
public class ModifierYaAsignadoException extends RuntimeException{
	public ModifierYaAsignadoException(String message) {
		super(message);
	}

}
