package com.facturacionAutonomo.documentacionfactura_service.infrastructure.client.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.facturacionAutonomo.documentacionfactura_service.domain.model.PaymentType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Cobro de un pedido, tal como lo devuelve product-service en GET /api/order/{orderPublicId}/payments
 * (su PaymentResult). La lista llega en el orden en que se cobró.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentResponse(
		UUID publicId,
		PaymentType paymentType,       // EFECTIVO | TARJETA
		BigDecimal amount,        // lo que este cobro aplica al pedido
		BigDecimal cashReceived,  // solo en efectivo: lo que entregó el cliente
		BigDecimal changeGiven,   // solo en efectivo: el cambio devuelto
		LocalDateTime createdAt
		) {
}
