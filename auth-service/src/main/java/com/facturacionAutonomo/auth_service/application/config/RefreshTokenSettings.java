package com.facturacionAutonomo.auth_service.application.config;

import java.time.Duration;

public record RefreshTokenSettings(Duration ttl) {
}
