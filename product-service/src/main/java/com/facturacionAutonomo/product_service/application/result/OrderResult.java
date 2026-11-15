package com.facturacionAutonomo.product_service.application.result;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.Order;
import com.facturacionAutonomo.product_service.domain.model.OrderStatus;
import com.facturacionAutonomo.product_service.domain.model.OrderType;
import com.facturacionAutonomo.product_service.domain.model.TableRestautant;


// El pedido tal como sale hacia fuera, con sus líneas. Lo devuelven el alta, las modificaciones y las consultas.
// La mesa sale por su publicId y su número, nunca por su id interno.
public record OrderResult(
		UUID publicId,
		Integer orderNumber,
		OrderType orderType,
		OrderStatus status,
		UUID tablePublicId,
		Integer tableNumber,
		Integer numPersonas,
		BigDecimal subtotal,      // base imponible (sin IVA)
		BigDecimal taxRate,       // tipo de IVA aplicado, en %
		BigDecimal taxAmount,     // cuota de IVA
		BigDecimal total,         // base + cuota = lo que paga el cliente
		BigDecimal paidAmount,    // lo cobrado hasta ahora
		BigDecimal pendingAmount, // lo que queda por pagar: el TPV sigue cobrando hasta que sea 0
		LocalDateTime paidAt,     // cuándo quedó pagado del todo (null si queda algo pendiente)
		List<OrderItemResult> items
		) {

	// mesa es null en los pedidos para llevar. El dominio Order solo guarda el id interno de la mesa:
	// el servicio, que ya la tiene cargada, la pasa para poder devolver su publicId y su número.
	public static OrderResult convertirA(Order order, TableRestautant mesa) {
		return new OrderResult(order.getPublicId(), order.getOrderNumber(), order.getOrderType(), order.getStatus(),
				mesa != null ? mesa.getPublicId() : null, mesa != null ? mesa.getTableNumber() : null,
				order.getNumPersonas(), order.getSubtotal(), order.getTaxRate(), order.getTaxAmount(),
				order.getTotal(), order.getPaidAmount(), order.getImportePendiente(), order.getPaidAt(),
				order.getItems().stream().map(OrderItemResult::convertirA).toList());
	}

}
