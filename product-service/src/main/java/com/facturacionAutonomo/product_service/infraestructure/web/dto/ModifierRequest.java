package com.facturacionAutonomo.product_service.infraestructure.web.dto;

import java.math.BigDecimal;

import com.facturacionAutonomo.product_service.domain.model.ModifierType;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo del alta de un modificador. El tenant llega en la cabecera X-Tenant-Id.
 * Los límites coinciden con las columnas de modifiers. priceDelta es opcional (0.00 por defecto)
 * y puede ser negativo.
 */
public class ModifierRequest {

	@NotBlank(message = "El nombre del modificador es obligatorio")
	@Size(max = 100, message = "El nombre del modificador no puede superar los 100 caracteres")
	private String name;

	// Un valor que no sea ANADIR, ELIMINAR o SUSTITUIR no se puede leer y se responde 400.
	@NotNull(message = "El tipo del modificador es obligatorio")
	private ModifierType modifierType;

	@Digits(integer = 8, fraction = 2, message = "El precio admite hasta 8 enteros y 2 decimales")
	private BigDecimal priceDelta;

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

}
