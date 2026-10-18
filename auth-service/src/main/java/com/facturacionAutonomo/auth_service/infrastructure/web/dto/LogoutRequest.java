package com.facturacionAutonomo.auth_service.infrastructure.web.dto;

public record LogoutRequest(String refreshToken) {

	@Override
	public String toString() {
		return "LogoutRequest[refreshToken=****]";
	}
}
