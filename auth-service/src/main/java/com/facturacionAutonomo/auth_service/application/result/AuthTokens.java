package com.facturacionAutonomo.auth_service.application.result;

import java.time.Duration;

public record AuthTokens(String accessToken, Duration accessTokenExpiresIn, String refreshToken) {
}
