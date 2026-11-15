package com.facturacionAutonomo.product_service.application.query;

import java.util.UUID;

// Los cobros de un pedido.
public record GetPaymentsQuery(
		UUID tenantId,
		UUID orderPublicId
		) {

}
