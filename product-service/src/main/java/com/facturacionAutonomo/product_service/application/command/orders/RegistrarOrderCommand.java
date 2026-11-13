package com.facturacionAutonomo.product_service.application.command.orders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.OrderType;

// Alta de un pedido con sus líneas (el camarero toma la nota y la envía entera a cocina).
// No lleva publicId, número de pedido, estado, precios ni totales: los pone el servidor.
public record RegistrarOrderCommand(
		UUID tenantId,
		OrderType orderType,
		UUID tablePublicId,             // obligatorio en COMERAQUI, null en LLEVAR
		Integer numPersonas,            // opcional; si llega, al menos 1
		String idempotencyKey,          // opcional; evita duplicados si el TPV reintenta el envío
		LocalDateTime clientTimestamp,  // opcional; hora del dispositivo al tomar la nota
		List<LineaOrderCommand> lineas  // al menos una
		) {

}
