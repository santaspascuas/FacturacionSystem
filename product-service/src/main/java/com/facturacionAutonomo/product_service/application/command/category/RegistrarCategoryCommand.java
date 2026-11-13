package com.facturacionAutonomo.product_service.application.command.category;

import java.util.UUID;

public record RegistrarCategoryCommand(
		UUID tenantId,
		String name) {

}
