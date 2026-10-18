package com.facturacionAutonomo.auth_service.domain.exception;

public class CredencialesInvalidasException extends DomainException {
	public CredencialesInvalidasException() {
		super("Email o contraseña incorrectos");
	}
}
