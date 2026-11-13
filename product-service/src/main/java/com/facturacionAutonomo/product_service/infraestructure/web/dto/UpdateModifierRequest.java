package com.facturacionAutonomo.product_service.infraestructure.web.dto;

import java.math.BigDecimal;

import com.facturacionAutonomo.product_service.domain.model.ModifierType;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo de la modificación de un modificador. El tenant llega en la cabecera X-Tenant-Id y el modificador en la ruta.
 * active es Boolean y no boolean: si no viene en el JSON se rechaza, en vez de desactivarlo sin querer.
 */
public class UpdateModifierRequest {

	@NotBlank(message = "El nombre del modificador es obligatorio")
	@Size(max = 100, message = "El nombre del modificador no puede superar los 100 caracteres")
	private String name;

	@NotNull(message = "El tipo del modificador es obligatorio")
	private ModifierType modifierType;

	@NotNull(message = "El precio del modificador es obligatorio")
	@Digits(integer = 8, fraction = 2, message = "El precio admite hasta 8 enteros y 2 decimales")
	private BigDecimal priceDelta;

	@NotNull(message = "El estado activo del modificador es obligatorio")
	private Boolean active;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ModifierType getModifierType() {
		return modifierType;
	}

	public void setModifierType(ModifierType modifierType) {
		this.modifierType = modifierType;
	}

	public BigDecimal getPriceDelta() {
		return priceDelta;
	}

	public void setPriceDelta(BigDecimal priceDelta) {
		this.priceDelta = priceDelta;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

}
