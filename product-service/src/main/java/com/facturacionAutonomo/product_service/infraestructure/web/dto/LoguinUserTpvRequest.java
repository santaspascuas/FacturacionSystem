package com.facturacionAutonomo.product_service.infraestructure.web.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Cuerpo de la petición de login de un usuario del TPV. El tenant llega en la cabecera X-Tenant-Id.
 */
public class LoguinUserTpvRequest {

	@NotBlank(message = "El nombre del usuario es obligatorio")
	private String username;

	@NotBlank(message = "La password es obligatoria")
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	// La contraseña nunca sale en los logs.
	@Override
	public String toString() {
		return "LoguinUserTpvRequest [username=" + username + ", password=****]";
	}
}
