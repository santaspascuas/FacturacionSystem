package com.facturacionAutonomo.auth_service.domain.exception;

public class RefreshTokenInvalidoException extends DomainException {
	public RefreshTokenInvalidoException() {
		super("Refresh token inválido o caducado");
	}
}
