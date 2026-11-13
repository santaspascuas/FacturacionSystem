package com.facturacionAutonomo.product_service.application.command.orders;

import java.util.UUID;

// Datos del pedido que se pueden cambiar después de abrirlo: solo el número de personas.
// El tipo y la mesa quedan fijos; las líneas se cambian con sus propios commands.
public record UpdateOrderCommand(
		UUID tenantId,
		UUID publicId,
		Integer numPersonas
		) {

}
