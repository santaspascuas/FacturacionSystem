package com.facturacionAutonomo.auth_service.domain.exception;

public class TenantNoEncontradoException extends DomainException {
	public TenantNoEncontradoException() {
		super("El tenant no existe");
	}
}
