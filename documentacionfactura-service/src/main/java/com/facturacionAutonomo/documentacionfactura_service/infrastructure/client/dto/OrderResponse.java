package com.facturacionAutonomo.documentacionfactura_service.infrastructure.client.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.facturacionAutonomo.documentacionfactura_service.domain.model.OrderStatus;
import com.facturacionAutonomo.documentacionfactura_service.domain.model.OrderType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Pedido tal como lo devuelve product-service en GET /api/order/{publicId} (su OrderResult).
 *
 * orderType y status los convierte Jackson directamente a los enums del dominio. Si product-service envía un valor
 * que no existe aquí, la lectura del JSON falla y ProductServiceCaller lo traduce a ServicioExternoException (502).
 * Los importes llevan el IVA incluido.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderResponse(
		UUID publicId,
		Integer orderNumber,
		OrderType orderType,         // COMERAQUI | LLEVAR
		OrderStatus status,            // ENVIADO | PREPARANDOCOMIDA | LISTO | CIERRAYPAGA | CANCELADO
		UUID tablePublicId,
		Integer tableNumber,      // null en los pedidos para llevar
		Integer numPersonas,
		BigDecimal subtotal,      // base imponible (sin IVA)
		BigDecimal taxRate,       // tipo de IVA aplicado, en %
		BigDecimal taxAmount,     // cuota de IVA
		BigDecimal total,         // base + cuota
		BigDecimal paidAmount,    // lo cobrado hasta ahora
		BigDecimal pendingAmount, // lo que queda por cobrar: 0 si está pagado del todo
		LocalDateTime paidAt,     // UTC; null mientras quede algo pendiente
		List<OrderItemResponse> items
		) {
}
