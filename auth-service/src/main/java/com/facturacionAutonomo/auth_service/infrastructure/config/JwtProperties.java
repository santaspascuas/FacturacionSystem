package com.facturacionAutonomo.auth_service.infrastructure.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@ConfigurationProperties(prefix = "app.security.jwt")
public record JwtProperties(
		String issuer,
		String audience,
		Duration accessTokenTtl,
		Duration refreshTokenTtl,
		String keyId,
		Resource privateKey) {
}
