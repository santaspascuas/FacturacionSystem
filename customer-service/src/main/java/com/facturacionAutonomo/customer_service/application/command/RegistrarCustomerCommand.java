package com.facturacionAutonomo.customer_service.application.command;

import java.util.UUID;

/**
 * Alta del customer, sin direcciones (se añaden después por su propio caso de uso).
 */
public record RegistrarCustomerCommand(UUID tenantId, String razonSocial, String nif, String email) {
}
