package com.facturacionAutonomo.documentacionfactura_service.infrastructure.client.mapper;

import java.util.List;

import com.facturacionAutonomo.documentacionfactura_service.domain.model.Order;
import com.facturacionAutonomo.documentacionfactura_service.domain.model.TicketLine;
import com.facturacionAutonomo.documentacionfactura_service.domain.model.TicketPayment;
import com.facturacionAutonomo.documentacionfactura_service.infrastructure.client.dto.OrderItemResponse;
import com.facturacionAutonomo.documentacionfactura_service.infrastructure.client.dto.OrderResponse;

/**
 * JSON del pedido de product-service → modelo de dominio. Los cobros llegan ya convertidos (PaymentPort).
 * Solo traduce: si los importes cuadran o el pedido está pagado lo comprueba Ticket.validarEmision, no el mapper.
 */
public final class OrderResponseMapper {

	private OrderResponseMapper() {
	}

	public static Order toDomain(OrderResponse pedido, List<TicketPayment> cobros) {
		return Order.restoreData(
				pedido.publicId(),
				pedido.orderNumber(),
				pedido.orderType(),
				pedido.status(),
				pedido.tableNumber(),
				pedido.numPersonas(),
				pedido.subtotal(),
				pedido.taxRate(),
				pedido.taxAmount(),
				pedido.total(),
				pedido.pendingAmount(),
				pedido.paidAt(),
				pedido.items() == null ? List.of() : pedido.items().stream().map(OrderResponseMapper::toLinea).toList(),
				cobros);
	}

	private static TicketLine toLinea(OrderItemResponse item) {
		return TicketLine.restoreData(item.productName(), item.quantity(), item.unitPrice(), item.lineTotal());
	}
}
