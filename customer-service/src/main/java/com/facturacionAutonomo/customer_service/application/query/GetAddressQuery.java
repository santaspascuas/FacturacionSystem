package com.facturacionAutonomo.customer_service.application.query;

import java.util.UUID;

/**
 * Consulta las direcciones de un tenant.
 */
public record GetAddressQuery(UUID tenantId) {

}
