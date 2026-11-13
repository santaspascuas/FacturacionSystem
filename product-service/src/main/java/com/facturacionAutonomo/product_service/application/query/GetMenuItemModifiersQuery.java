package com.facturacionAutonomo.product_service.application.query;

import java.util.UUID;

public record GetMenuItemModifiersQuery(
		UUID tenantId,
		UUID menuItemPublicId
		) {

}
