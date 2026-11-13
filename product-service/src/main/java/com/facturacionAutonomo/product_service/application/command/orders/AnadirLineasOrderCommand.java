package com.facturacionAutonomo.product_service.application.command.orders;

import java.util.List;
import java.util.UUID;

// Añadir platos a un pedido que ya está abierto (p. ej. el cliente pide el postre).
public record AnadirLineasOrderCommand(
		UUID tenantId,
		UUID orderPublicId,
		List<LineaOrderCommand> lineas  // al menos una
		) {

}
