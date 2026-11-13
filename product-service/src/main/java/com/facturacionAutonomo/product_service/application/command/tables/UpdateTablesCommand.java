package com.facturacionAutonomo.product_service.application.command.tables;

import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.TableZone;

// Datos de la mesa. El estado se cambia aparte, con CambiarEstadoTablesCommand.
public record UpdateTablesCommand(
        UUID tenantId,
        UUID publicId,
        Integer tableNumber,
        Integer capacity,
        TableZone zone
		) {

}
