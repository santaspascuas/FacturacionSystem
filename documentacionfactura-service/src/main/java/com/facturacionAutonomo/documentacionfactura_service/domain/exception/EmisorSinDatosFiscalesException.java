package com.facturacionAutonomo.documentacionfactura_service.domain.exception;

/** El restaurante no tiene razón social o NIF en customer-service: un ticket sin ellos no es válido. */
public class EmisorSinDatosFiscalesException extends RuntimeException {
	public EmisorSinDatosFiscalesException(String message) {
		super(message);
	}
}
