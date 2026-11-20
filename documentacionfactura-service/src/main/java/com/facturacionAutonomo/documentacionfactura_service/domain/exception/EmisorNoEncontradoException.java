package com.facturacionAutonomo.documentacionfactura_service.domain.exception;

/** El restaurante no está registrado en customer-service: no hay datos fiscales que consultar (404). */
public class EmisorNoEncontradoException extends RuntimeException {
	public EmisorNoEncontradoException(String message) {
		super(message);
	}
}
