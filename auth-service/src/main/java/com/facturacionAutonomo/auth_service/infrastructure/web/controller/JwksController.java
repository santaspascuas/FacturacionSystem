package com.facturacionAutonomo.auth_service.infrastructure.web.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;

@RestController
public class JwksController {
	private final Map<String, Object> jwksPublico;

	public JwksController(RSAKey rsaKey) {
		this.jwksPublico = new JWKSet(rsaKey.toPublicJWK()).toJSONObject();
	}

	@GetMapping("/.well-known/jwks.json")
	public Map<String, Object> jwks() {
		return jwksPublico;
	}
}
