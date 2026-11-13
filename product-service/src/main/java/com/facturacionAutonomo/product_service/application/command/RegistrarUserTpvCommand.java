package com.facturacionAutonomo.product_service.application.command;

import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.Role;

public record RegistrarUserTpvCommand(
		UUID tenantId,
		String username,
		String password_hash
		) {

}
