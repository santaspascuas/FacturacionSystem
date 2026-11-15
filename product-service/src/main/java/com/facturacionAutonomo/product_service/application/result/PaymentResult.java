package com.facturacionAutonomo.product_service.application.result;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.Payment;
import com.facturacionAutonomo.product_service.domain.model.PaymentType;

// Un cobro tal como sale hacia fuera. Sin ids internos ni tenant: el cobro se identifica por su publicId.
// Lo usan el listado de cobros de un pedido y, dentro de RegistrarPaymentResult, el registro de un cobro.
public record PaymentResult(
		UUID publicId,
		PaymentType paymentType,
		BigDecimal amount,          // lo que este cobro aplica al pedido
		BigDecimal cashReceived,    // solo en efectivo: lo que entregó el cliente
		BigDecimal changeGiven,     // solo en efectivo: el cambio que hay que devolver
		LocalDateTime createdAt
		) {

	public static PaymentResult convertirA(Payment payment) {
		return new PaymentResult(payment.getPublicId(), payment.getPaymentType(), payment.getAmount(),
				payment.getCashReceived(), payment.getChangeGiven(), payment.getCreatedAt());
	}

}
