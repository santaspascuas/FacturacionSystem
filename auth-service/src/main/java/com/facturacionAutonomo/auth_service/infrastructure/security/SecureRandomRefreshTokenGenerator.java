package com.facturacionAutonomo.auth_service.infrastructure.security;

import java.security.SecureRandom;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.facturacionAutonomo.auth_service.application.port.out.RefreshTokenGeneratorPort;

@Component
public class SecureRandomRefreshTokenGenerator implements RefreshTokenGeneratorPort {
	private static final int BYTES = 32;

	private static final Logger log = LoggerFactory.getLogger(SecureRandomRefreshTokenGenerator.class);

	private final SecureRandom secureRandom = new SecureRandom();

	private final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

	@Override
	public String generate() {
		log.info("[generate] Generando refresh token");

		byte[] bytes = new byte[BYTES];
		secureRandom.nextBytes(bytes);
		String token = encoder.encodeToString(bytes);

		log.debug("[generate] Refresh token generado ({} caracteres)", token.length());
		return token;
	}
}
