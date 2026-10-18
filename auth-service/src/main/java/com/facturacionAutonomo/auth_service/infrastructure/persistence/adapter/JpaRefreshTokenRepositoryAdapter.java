package com.facturacionAutonomo.auth_service.infrastructure.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.auth_service.application.port.out.RefreshTokenRepositoryPort;
import com.facturacionAutonomo.auth_service.domain.exception.RefreshTokenInvalidoException;
import com.facturacionAutonomo.auth_service.domain.model.RefreshToken;
import com.facturacionAutonomo.auth_service.infrastructure.persistence.entity.RefreshTokenEntity;
import com.facturacionAutonomo.auth_service.infrastructure.persistence.entity.UserEntity;
import com.facturacionAutonomo.auth_service.infrastructure.persistence.mapper.RefreshTokenEntityMapper;
import com.facturacionAutonomo.auth_service.infrastructure.persistence.repository.SpringDataRefreshTokenRepository;
import com.facturacionAutonomo.auth_service.infrastructure.persistence.repository.SpringDataUserRepository;

@Repository
public class JpaRefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort{
	private final SpringDataRefreshTokenRepository springdataRefresco;
	private final SpringDataUserRepository springdataUser;
	private static final Logger log = LoggerFactory.getLogger(JpaRefreshTokenRepositoryAdapter.class);

	public JpaRefreshTokenRepositoryAdapter(SpringDataRefreshTokenRepository springdataRefresco,
			SpringDataUserRepository springdataUser) {
		super();
		this.springdataRefresco = springdataRefresco;
		this.springdataUser = springdataUser;
	}

	@Override
	@Transactional
	public RefreshToken save(RefreshToken refreshToken) {
		log.info("[Activamos la persistencia->Guardamos->Inicio] userId: {}", refreshToken.getUserId());
		log.debug("[save] tokenHash: {}... revoked: {} expiresAt: {}", prefijo(refreshToken.getTokenHash()),
				refreshToken.isRevoked(), refreshToken.getExpiresAt());

		RefreshTokenEntity refrescoEntity = springdataRefresco.findByUserEntidadPublicId(refreshToken.getUserId())
				.orElse(null);

		UserEntity usuario;
		if (refrescoEntity == null) {
			usuario = springdataUser.findByPublicId(refreshToken.getUserId()).orElse(null);
			if (usuario == null) {
				throw new IllegalStateException("El usuario del refresh token no existe");
			}
			refrescoEntity = new RefreshTokenEntity();
			log.debug("[save] No existe fila para el usuario -> se crea");
		} else {
			usuario = refrescoEntity.getUserEntidad();
			log.debug("[save] Existe fila -> se actualiza: {}", refrescoEntity);
		}
		RefreshTokenEntityMapper.dominioToJpa(refreshToken, usuario, refrescoEntity);

		try {
			RefreshTokenEntity guardado = springdataRefresco.saveAndFlush(refrescoEntity);
			log.debug("[save] Guardado: {}", guardado);
			return RefreshTokenEntityMapper.jpaToDominio(guardado);
		} catch (ObjectOptimisticLockingFailureException ex) {
			log.warn("[save] Conflicto de version al guardar el refresh token del usuario {}", refreshToken.getUserId());
			throw new RefreshTokenInvalidoException();
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<RefreshToken> findByTokenHash(String tokenHash) {
		log.info("[findByTokenHash] tokenHash: {}...", prefijo(tokenHash));
		Optional<RefreshToken> encontrado = springdataRefresco.findByTokenHash(tokenHash)
				.map(RefreshTokenEntityMapper::jpaToDominio);
		log.debug("[findByTokenHash] encontrado: {}", encontrado.isPresent());
		return encontrado;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<RefreshToken> findByUserId(UUID userId) {
		log.info("[findByUserId] userId: {}", userId);
		Optional<RefreshToken> encontrado = springdataRefresco.findByUserEntidadPublicId(userId)
				.map(RefreshTokenEntityMapper::jpaToDominio);
		log.debug("[findByUserId] encontrado: {}", encontrado.isPresent());
		return encontrado;
	}

	private static String prefijo(String hash) {
		return hash == null ? "null" : hash.substring(0, Math.min(8, hash.length()));
	}
}
