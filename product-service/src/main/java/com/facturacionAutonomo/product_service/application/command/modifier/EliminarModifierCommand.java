package com.facturacionAutonomo.product_service.application.command.modifier;

import java.util.UUID;

public record EliminarModifierCommand(
		UUID tenantId,
		UUID publicId
		) {

}
