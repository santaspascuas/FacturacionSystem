package com.facturacionAutonomo.product_service.application.command.tables;

import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.TableStatus;

// Solo el estado de la mesa: lo que cambian los camareros durante el servicio.
public record CambiarEstadoTablesCommand(
		UUID tenantId,
		UUID publicId,
		TableStatus status
		) {

}
