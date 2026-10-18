package com.facturacionAutonomo.auth_service.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.facturacionAutonomo.auth_service.application.port.out.TokenHasherPort;

@Component
public class Sha256TokenHasher implements TokenHasherPort {
	private static final String ALGORITMO = "SHA-256";

	private static final Logger log = LoggerFactory.getLogger(Sha256TokenHasher.class);

	@Override
	public String hash(String rawToken) {
		log.info("[hash] Calculando hash del refresh token");

		if (rawToken == null) {
			log.warn("[hash] Se ha intentado hashear un token null");
			throw new IllegalArgumentException("El token a hashear no puede ser null");
		}
		try {
			MessageDigest digest = MessageDigest.getInstance(ALGORITMO);
			byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
			String hashHex = HexFormat.of().formatHex(hash);
			log.debug("[hash] Hash calculado: {}... ({} caracteres)", hashHex.substring(0, 8), hashHex.length());
			return hashHex;
		} catch (NoSuchAlgorithmException e) {
			log.error("[hash] {} no disponible en la JVM", ALGORITMO, e);
			throw new IllegalStateException(ALGORITMO + " no disponible en la JVM", e);
		}
	}
}
