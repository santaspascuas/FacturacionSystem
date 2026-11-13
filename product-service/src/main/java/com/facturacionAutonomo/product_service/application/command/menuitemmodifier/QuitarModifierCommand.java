package com.facturacionAutonomo.product_service.application.command.menuitemmodifier;

import java.util.UUID;

public record QuitarModifierCommand(
		UUID tenantId,
		UUID menuItemPublicId,
		UUID modifierPublicId
		) {

}
