package com.facturacionAutonomo.product_service.infraestructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo de la modificación de una categoría. El tenant llega en la cabecera X-Tenant-Id y la categoría en la ruta.
 * active es Boolean y no boolean: si no viene en el JSON se rechaza, en vez de desactivar la categoría sin querer.
 */
public class UpdateCategoryRequest {

	@NotBlank(message = "El nombre de la categoría es obligatorio")
	@Size(max = 100, message = "El nombre de la categoría no puede superar los 100 caracteres")
	private String name;

	@NotNull(message = "El estado activo de la categoría es obligatorio")
	private Boolean active;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

}
