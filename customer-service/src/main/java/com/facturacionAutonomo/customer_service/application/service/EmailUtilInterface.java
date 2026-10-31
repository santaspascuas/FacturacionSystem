package com.facturacionAutonomo.customer_service.application.service;

public interface EmailUtilInterface {

	/** Comprueba el formato del email ya normalizado. */
	boolean esValido(String email);

}
