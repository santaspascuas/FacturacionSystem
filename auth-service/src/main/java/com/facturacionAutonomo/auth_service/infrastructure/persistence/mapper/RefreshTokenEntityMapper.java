package com.facturacionAutonomo.auth_service.infrastructure.persistence.mapper;

import com.facturacionAutonomo.auth_service.domain.model.RefreshToken;
import com.facturacionAutonomo.auth_service.infrastructure.persistence.entity.RefreshTokenEntity;
import com.facturacionAutonomo.auth_service.infrastructure.persistence.entity.UserEntity;

public class RefreshTokenEntityMapper {
	private RefreshTokenEntityMapper() {}

	public static RefreshTokenEntity dominioToJpa(RefreshToken refreshToken, UserEntity usuario, RefreshTokenEntity destino) {
		destino.setUserEntidad(usuario);
		destino.setTokenHash(refreshToken.getTokenHash());
		destino.setExpiresAt(refreshToken.getExpiresAt());
		destino.setCreatedAt(refreshToken.getCreatedAt());
		destino.setRevoked(refreshToken.isRevoked());
		return destino;
	}

	public static RefreshToken jpaToDominio(RefreshTokenEntity entity) {
		return RefreshToken.restoreData(entity.getUserEntidad().getPublicId(), entity.getTokenHash(),
				entity.getExpiresAt(), entity.getCreatedAt(), entity.isRevoked());
	}
}
