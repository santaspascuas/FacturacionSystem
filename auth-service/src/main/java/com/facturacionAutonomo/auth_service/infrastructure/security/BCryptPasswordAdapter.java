package com.facturacionAutonomo.auth_service.infrastructure.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.facturacionAutonomo.auth_service.application.port.out.PasswordHasherPort;

@Component
public class BCryptPasswordAdapter implements PasswordHasherPort{
	private static final Logger log = LoggerFactory.getLogger(BCryptPasswordAdapter.class);

	private final PasswordEncoder passwordEncoder;

	public BCryptPasswordAdapter(PasswordEncoder passwordEncoder) {
		super();
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public String hash(String rawPassword) {
		log.info("[String hash--Funcion] Hasheando contraseña");
		return passwordEncoder.encode(rawPassword);
	}

	@Override
	public boolean matches(String rawPassword, String passwordHash) {
		log.info("[Funcion-Comparacion-passwords: boolean matches] Comparando contraseña");
		boolean coincide = passwordEncoder.matches(rawPassword, passwordHash);
		log.debug("[matches] coincide: {}", coincide);
		return coincide;
	}
}
