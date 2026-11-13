package com.facturacionAutonomo.product_service.application.command.menuitemmodifier;

import java.util.UUID;

// Cambia las reglas con las que un producto admite un modificador.
public record UpdateMenuItemModifierCommand(
		UUID tenantId,
		UUID menuItemPublicId,
		UUID modifierPublicId,
		int maxSelections,
		boolean required
		) {

}
