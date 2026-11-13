package com.facturacionAutonomo.product_service.application.command;

import java.util.UUID;

public record LoguinUserTpvCommand(
		UUID tenantId,
		String username,
		String password_hash
		) {

}
