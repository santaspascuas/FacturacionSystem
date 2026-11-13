package com.facturacionAutonomo.product_service.application.result;

import java.math.BigDecimal;
import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.Modifier;
import com.facturacionAutonomo.product_service.domain.model.ModifierType;

public record ModifierResult(
		UUID publicId,
		String name,
		ModifierType modifierType,
		BigDecimal priceDelta,
		boolean active
		) {

	public static ModifierResult convertirA(Modifier modifier) {
		return new ModifierResult(modifier.getPublicId(), modifier.getName(), modifier.getModifierType(),
				modifier.getPriceDelta(), modifier.isActive());
	}

}
