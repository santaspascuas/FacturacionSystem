package com.facturacionAutonomo.product_service.application.command.orders;

import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.OrderStatus;

// Avanzar el pedido (COMANDALANZADA, PREPARANDOCOMIDA, LISTO), cerrarlo y cobrarlo (CIERRAYPAGA) o anularlo (CANCELADO).
public record CambiarEstadoOrderCommand(
		UUID tenantId,
		UUID orderPublicId,
		OrderStatus status
		) {

}
