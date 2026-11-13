package com.facturacionAutonomo.product_service.application.result;

import java.math.BigDecimal;
import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.MenuItemModifier;
import com.facturacionAutonomo.product_service.domain.model.Modifier;
import com.facturacionAutonomo.product_service.domain.model.ModifierType;

// Un modificador que admite un producto: sus datos y las reglas de la asociación. Los ids internos no salen.
public record MenuItemModifierResult(
		UUID modifierPublicId,
		String name,
		ModifierType modifierType,
		BigDecimal priceDelta,
		int maxSelections,
		boolean required
		) {

	public static MenuItemModifierResult convertirA(MenuItemModifier asociacion, Modifier modifier) {
		return new MenuItemModifierResult(modifier.getPublicId(), modifier.getName(), modifier.getModifierType(),
				modifier.getPriceDelta(), asociacion.getMaxSelections(), asociacion.isRequired());
	}

}
