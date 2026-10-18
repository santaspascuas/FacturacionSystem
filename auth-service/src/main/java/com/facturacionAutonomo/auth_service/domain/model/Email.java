package com.facturacionAutonomo.auth_service.domain.model;

import com.facturacionAutonomo.auth_service.domain.exception.DatoInvalidoException;

import java.util.Locale;
import java.util.regex.Pattern;

public record Email(String value) {
	private static final Pattern FORMATO = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

	public Email {
		if (value == null || value.isBlank()) {
			throw new DatoInvalidoException("El email es obligatorio");
		}
		value = value.trim().toLowerCase(Locale.ROOT);
		if (!FORMATO.matcher(value).matches()) {
			throw new DatoInvalidoException("Email inválido");
		}
	}

	@Override
	public String toString() {
		return value;
	}
}
