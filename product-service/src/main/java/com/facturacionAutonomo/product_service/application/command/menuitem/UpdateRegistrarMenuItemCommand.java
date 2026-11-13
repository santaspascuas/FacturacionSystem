package com.facturacionAutonomo.product_service.application.command.menuitem;

import java.math.BigDecimal;
import java.util.UUID;

// menuItemPublicId es el producto que se modifica; categoryPublicId, la categoría en la que queda.
public record UpdateRegistrarMenuItemCommand(
		UUID tenantId,
		UUID menuItemPublicId,
		UUID categoryPublicId,
		String name,
		String description,
		BigDecimal basePrice,
		String imageUrl,
		boolean isactive

		) {

}
