package com.facturacionAutonomo.customer_service.application.query;

import java.util.UUID;

/**
 * Consulta del customer del tenant. Query y no command: solo lee, no modifica nada.
 */
public record GetCustomerQuery(UUID tenantId) {
}
