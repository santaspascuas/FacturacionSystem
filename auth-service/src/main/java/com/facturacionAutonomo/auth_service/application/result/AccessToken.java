package com.facturacionAutonomo.auth_service.application.result;

import java.time.Duration;

public record AccessToken(String value, Duration expiresIn) {
}
