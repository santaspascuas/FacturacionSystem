package com.facturacionAutonomo.product_service.application.command.modifier;

import java.math.BigDecimal;
import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.ModifierType;

// priceDelta es opcional: si es null se guarda 0.00, el DEFAULT de modifiers.price_delta.
public record RegistrarModifierCommand(
		UUID tenantId,
		String name,
		ModifierType modifierType,
		BigDecimal priceDelta
		) {

}
