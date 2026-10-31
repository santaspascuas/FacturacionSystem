package com.facturacionAutonomo.customer_service.application.command;

import java.util.UUID;

public record UpdateCustomerCommand(UUID tenantId, String razonSocial, String nif) {

}
