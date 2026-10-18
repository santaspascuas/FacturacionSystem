package com.facturacionAutonomo.auth_service.domain.exception;

public class CuentaExpiradaException extends DomainException {
	public CuentaExpiradaException() {
		super("La cuenta ha expirado");
	}
}
