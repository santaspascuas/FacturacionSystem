package com.facturacionAutonomo.product_service.application.query;

import java.util.UUID;

// categoryPublicId es opcional: si es null se listan todos los productos del tenant.
public record GetMenuItemsQuery(
		UUID tenantId,
		UUID categoryPublicId
		) {

}
