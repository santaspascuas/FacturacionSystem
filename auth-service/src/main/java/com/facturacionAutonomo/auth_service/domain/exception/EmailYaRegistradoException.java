package com.facturacionAutonomo.auth_service.domain.exception;

public class EmailYaRegistradoException extends DomainException {
	public EmailYaRegistradoException() {
		super("El email ya está registrado");
	}
}
