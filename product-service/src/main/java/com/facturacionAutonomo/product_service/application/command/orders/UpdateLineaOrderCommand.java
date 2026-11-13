package com.facturacionAutonomo.product_service.application.command.orders;

import java.util.UUID;

// Cambiar la cantidad o las notas de una línea. El producto y su precio no cambian:
// para otro producto se quita la línea y se añade una nueva.
public record UpdateLineaOrderCommand(
		UUID tenantId,
		UUID orderPublicId,
		UUID itemPublicId,
		Integer quantity,
		String notes
		) {

}
