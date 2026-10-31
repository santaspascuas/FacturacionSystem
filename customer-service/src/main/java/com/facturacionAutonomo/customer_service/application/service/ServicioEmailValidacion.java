package com.facturacionAutonomo.customer_service.application.service;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ServicioEmailValidacion implements EmailUtilInterface {

	private static final Logger log = LoggerFactory.getLogger(ServicioEmailValidacion.class);

	private final String[] dominiosPermitidos = {"gmail.com", "hotmail.com"};

	@Override
	public boolean esValido(String email) {
		if (email == null || email.isBlank()) {
			return false;
		}

		// Solo para comparar: el email guardado no se modifica.
		String comparable = email.trim().toLowerCase(Locale.ROOT);

		for (String dominio : dominiosPermitidos) {
			if (comparable.endsWith("@" + dominio)) {
				log.debug("[esValido] dominio permitido: {}", dominio);
				return true;
			}
		}

		log.debug("[esValido] dominio no permitido: {}", email);
		return false;
	}
}
