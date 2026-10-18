package com.facturacionAutonomo.auth_service.application.port.out;

import java.util.Optional;
import java.util.UUID;

import com.facturacionAutonomo.auth_service.domain.model.RefreshToken;

public interface RefreshTokenRepositoryPort {
	RefreshToken save(RefreshToken refreshToken);

	Optional<RefreshToken> findByTokenHash(String tokenHash);

	Optional<RefreshToken> findByUserId(UUID userId);
}
