package com.facturacionAutonomo.auth_service.application.result;

import java.util.UUID;

public record RegisterUserResult(UUID id, UUID tenantId, String role) {
}
