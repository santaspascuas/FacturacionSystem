package com.facturacionAutonomo.product_service.application.query;

import java.util.UUID;

public record GetOrderQuery(
		UUID tenantId,
		UUID publicId
		) {

}
