package com.facturacionAutonomo.auth_service.domain.exception;

public class CuentaBloqueadaException extends DomainException {
	public CuentaBloqueadaException() {
		super("La cuenta está bloqueada");
	}
}
