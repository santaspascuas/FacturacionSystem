package com.facturacionAutonomo.product_service.application.command.orders;

import java.util.UUID;

// Quitar un plato del pedido. El pedido en sí no se borra nunca (es historial de facturación):
// para anularlo entero se cambia su estado a CANCELADO con CambiarEstadoOrderCommand.
public record EliminarLineaOrderCommand(
		UUID tenantId,
		UUID orderPublicId,
		UUID itemPublicId
		) {

}
