package com.facturacionAutonomo.auth_service.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
		@NotBlank(message = "El refresh token es obligatorio")
		String refreshToken) {

	@Override
	public String toString() {
		return "RefreshRequest[refreshToken=****]";
	}
}
