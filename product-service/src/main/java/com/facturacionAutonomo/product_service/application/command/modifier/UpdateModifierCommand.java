package com.facturacionAutonomo.product_service.application.command.modifier;

import java.math.BigDecimal;
import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.ModifierType;

// modifierPublicId es el modificador que se modifica.
public record UpdateModifierCommand(
		UUID tenantId,
		UUID modifierPublicId,
		String name,
		ModifierType modifierType,
		BigDecimal priceDelta,
		boolean isactive
		) {

}
