package com.facturacionAutonomo.product_service.application.command.menuitemmodifier;

import java.util.UUID;

// maxSelections y required son opcionales: si son null se usan los DEFAULT de menu_item_modifiers (1 y false).
public record AsignarModifierCommand(
		UUID tenantId,
		UUID menuItemPublicId,
		UUID modifierPublicId,
		Integer maxSelections,
		Boolean required
		) {

}
