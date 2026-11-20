package com.facturacionAutonomo.documentacionfactura_service.application.query;

import java.util.UUID;

public record GetOrderQuery(UUID tenantId, UUID orderPublicId) {

}
