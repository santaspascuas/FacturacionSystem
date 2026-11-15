package com.facturacionAutonomo.product_service.application.command.orders;

import java.util.List;
import java.util.UUID;

// Añadir platos a un pedido que ya está abierto (p. ej. el cliente pide el postre).
// idempotencyKey identifica este envío del TPV: si lo reintenta con la misma clave, las líneas no se duplican.
public record AnadirLineasOrderCommand(
		UUID tenantId,
		UUID orderPublicId,
		String idempotencyKey,          // opcional
		List<LineaOrderCommand> lineas  // al menos una
		) {

}
