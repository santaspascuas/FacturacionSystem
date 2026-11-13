package com.facturacionAutonomo.product_service.infraestructure.web.dto;

import jakarta.validation.constraints.NotBlank;

public class UsertpvRequest {
	
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
	
	
	
	

}
