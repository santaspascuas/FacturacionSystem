package com.facturacionAutonomo.product_service.application.command.orders;

import java.util.UUID;

// Una línea tal como la envía el TPV: qué producto, cuántos y notas para cocina.
// Sin nombre ni precio: los copia el servidor del producto (el cliente dice qué quiere, el servidor cuánto cuesta).
public record LineaOrderCommand(
        UUID menuItemPublicId,
        Integer quantity,
        String notes
		) {

}
