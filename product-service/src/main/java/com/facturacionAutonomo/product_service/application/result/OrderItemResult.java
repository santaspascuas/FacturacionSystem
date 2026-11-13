package com.facturacionAutonomo.product_service.application.result;

import java.math.BigDecimal;
import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.OrderItem;

// Una línea del pedido tal como sale hacia fuera. Sin ids internos: la línea se identifica por su publicId.
public record OrderItemResult(
		UUID publicId,
		String productName,
		BigDecimal unitPrice,
		int quantity,
		BigDecimal lineTotal,
		String notes
		) {

	public static OrderItemResult convertirA(OrderItem item) {
		return new OrderItemResult(item.getPublicId(), item.getProductName(), item.getUnitPrice(), item.getQuantity(),
				item.getLineTotal(), item.getNotes());
	}

}
