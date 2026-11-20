package com.facturacionAutonomo.documentacionfactura_service.infrastructure.client.mapper;

import java.util.List;

import com.facturacionAutonomo.documentacionfactura_service.domain.model.TicketPayment;
import com.facturacionAutonomo.documentacionfactura_service.infrastructure.client.dto.PaymentResponse;

/**
 * Cobros de product-service → TicketPayment. Solo se copia lo que sale en el ticket: publicId y createdAt del cobro
 * no se usan.
 */
public final class PaymentResponseMapper {

	private PaymentResponseMapper() {
	}

	public static List<TicketPayment> toDomain(List<PaymentResponse> cobros) {
		return cobros == null ? List.of() : cobros.stream().map(PaymentResponseMapper::toCobro).toList();
	}

	private static TicketPayment toCobro(PaymentResponse cobro) {
		return TicketPayment.restoreData(
				cobro.paymentType(),
				cobro.amount(),
				cobro.cashReceived(),
				cobro.changeGiven());
	}
}
