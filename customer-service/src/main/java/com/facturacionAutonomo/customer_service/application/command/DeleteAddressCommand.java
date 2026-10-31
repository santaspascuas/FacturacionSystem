package com.facturacionAutonomo.customer_service.application.command;

import java.util.UUID;

public record DeleteAddressCommand(UUID tenantId, UUID publicId) {

}
