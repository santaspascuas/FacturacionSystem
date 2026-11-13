package com.facturacionAutonomo.product_service.infraestructure.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Cuerpo del cambio de reglas de un modificador en un producto. El producto y el modificador llegan en la ruta.
 * Los dos campos son obligatorios: si faltaran, se cambiarían las reglas sin querer.
 */
public class UpdateMenuItemModifierRequest {

	@NotNull(message = "El número máximo de selecciones es obligatorio")
	@Min(value = 1, message = "El número máximo de selecciones tiene que ser al menos 1")
	private Integer maxSelections;

	@NotNull(message = "Indicar si el modificador es obligatorio es obligatorio")
	private Boolean required;

	public Integer getMaxSelections() {
		return maxSelections;
	}

	public void setMaxSelections(Integer maxSelections) {
		this.maxSelections = maxSelections;
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

}
