package com.facturacionAutonomo.product_service.application.command.category;

import java.util.UUID;

public record UpdateCategoryCommand(
		UUID tenantId,
		UUID publicId,
		String name,
		boolean activo
		) {

}
