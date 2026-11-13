package com.facturacionAutonomo.product_service.application.query;

import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.TableStatus;

// status es opcional: si es null se listan todas las mesas del tenant; con DISPONIBLE, solo las libres.
public record GetTablesQuery(
		UUID tenantId,
		TableStatus status
		) {

}
