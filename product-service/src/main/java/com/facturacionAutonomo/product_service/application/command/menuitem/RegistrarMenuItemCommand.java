package com.facturacionAutonomo.product_service.application.command.menuitem;

import java.math.BigDecimal;
import java.util.UUID;

// La categoría llega por su publicId, que es el que se expone fuera; el servicio la resuelve a su id interno.
public record RegistrarMenuItemCommand(
		UUID tenantId,
		UUID categoryPublicId,
		String name,
		String description,
		BigDecimal basePrice,
		String imageUrl
		) {

}
