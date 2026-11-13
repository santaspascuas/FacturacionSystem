package com.facturacionAutonomo.product_service.application.command.tables;

import java.util.UUID;

public record EliminarTablesCommand(
		UUID tenantId,
		UUID publicId
		) {

}
