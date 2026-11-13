package com.facturacionAutonomo.product_service.application.command.menuitem;

import java.util.UUID;

public record EliminarMenuItemCommand(
		UUID tenantId,
		UUID publicId
		) {

}
