package com.facturacionAutonomo.auth_service.infrastructure.web.dto;

import com.facturacionAutonomo.auth_service.application.result.AuthTokens;

public record TokenResponse(String accessToken, String tokenType, long expiresIn, String refreshToken) {

	private static final String BEARER = "Bearer";

	public static TokenResponse from(AuthTokens tokens) {
		return new TokenResponse(tokens.accessToken(), BEARER, tokens.accessTokenExpiresIn().toSeconds(),
				tokens.refreshToken());
	}

	@Override
	public String toString() {
		return "TokenResponse[tokenType=" + tokenType + ", expiresIn=" + expiresIn + "]";
	}
}
