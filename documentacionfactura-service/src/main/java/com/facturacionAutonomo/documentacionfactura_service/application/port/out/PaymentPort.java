package com.facturacionAutonomo.documentacionfactura_service.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.facturacionAutonomo.documentacionfactura_service.domain.model.TicketPayment;

/** Puerto de salida hacia los cobros de un pedido de product-service. */
public interface PaymentPort {

	/**
	 * Cobros del pedido, en el orden en que se cobraron. Lista vacía si el pedido existe pero no tiene cobros.
	 * Optional vacío si el pedido no existe para ese tenant: así "no existe" no se confunde con "sin cobros".
	 */
	Optional<List<TicketPayment>> listarPagos(UUID tenantId, UUID orderPublicId);
}
