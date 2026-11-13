package com.facturacionAutonomo.product_service.infraestructure.persistence.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.facturacionAutonomo.product_service.application.port.OUT.PasswordHasherPort;

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
		// TODO Auto-generated method stub
		return passwordEncoder.encode(rawPassword);
	}

	@Override
	public boolean matches(String rawPassword, String passwordHash) {
		log.info("[Funcion-Comparacion-passwords: boolean matches] Comparando contraseña");
		// TODO Auto-generated method stub
		return passwordEncoder.matches(rawPassword, passwordHash);
	}

}
