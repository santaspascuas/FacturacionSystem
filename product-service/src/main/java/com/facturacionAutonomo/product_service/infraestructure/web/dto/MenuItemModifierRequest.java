package com.facturacionAutonomo.product_service.infraestructure.web.dto;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Cuerpo de la asignación de un modificador a un producto. El tenant llega en la cabecera X-Tenant-Id
 * y el producto en la ruta. maxSelections y required son opcionales (1 y false por defecto, como en la BD).
 */
public class MenuItemModifierRequest {

	@NotNull(message = "El modificador es obligatorio")
	private UUID modifierPublicId;

	@Min(value = 1, message = "El número máximo de selecciones tiene que ser al menos 1")
	private Integer maxSelections;

	private Boolean required;

	public UUID getModifierPublicId() {
		return modifierPublicId;
	}

	public void setModifierPublicId(UUID modifierPublicId) {
		this.modifierPublicId = modifierPublicId;
	}

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
