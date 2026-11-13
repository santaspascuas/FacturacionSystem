package com.facturacionAutonomo.product_service.infraestructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo del alta de una categoría. El tenant llega en la cabecera X-Tenant-Id y la posición en la carta la pone el servicio.
 * El límite coincide con la columna name de menu_categories: si se supera, el error llegaría desde la BD.
 */
public class CategoryRequest {

	@NotBlank(message = "El nombre de la categoría es obligatorio")
	@Size(max = 100, message = "El nombre de la categoría no puede superar los 100 caracteres")
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
