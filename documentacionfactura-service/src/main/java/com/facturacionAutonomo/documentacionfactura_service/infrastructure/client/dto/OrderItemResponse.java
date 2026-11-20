package com.facturacionAutonomo.documentacionfactura_service.infrastructure.client.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Línea de un pedido de product-service (su OrderItemResult). Precios con IVA incluido. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderItemResponse(
		UUID publicId,
		String productName,
		BigDecimal unitPrice,
		int quantity,
		BigDecimal lineTotal,
		String notes             // para cocina; no se imprime en el ticket
		) {
}
