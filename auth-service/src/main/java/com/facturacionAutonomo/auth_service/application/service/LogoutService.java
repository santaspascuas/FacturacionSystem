package com.facturacionAutonomo.auth_service.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.auth_service.application.command.LogoutCommand;
import com.facturacionAutonomo.auth_service.application.port.in.LogoutUseCase;
import com.facturacionAutonomo.auth_service.application.port.out.RefreshTokenRepositoryPort;
import com.facturacionAutonomo.auth_service.application.port.out.TokenHasherPort;
import com.facturacionAutonomo.auth_service.domain.model.RefreshToken;

@Service
public class LogoutService implements LogoutUseCase {
	private static final Logger log = LoggerFactory.getLogger(LogoutService.class);

	private final RefreshTokenRepositoryPort refreshTokenRepository;
	private final TokenHasherPort tokenHasher;

	public LogoutService(RefreshTokenRepositoryPort refreshTokenRepository, TokenHasherPort tokenHasher) {
		this.refreshTokenRepository = refreshTokenRepository;
		this.tokenHasher = tokenHasher;
	}

	@Override
	@Transactional
	public void execute(LogoutCommand command) {
		log.info("[logout] Inicio");

		if (command.refreshToken() == null || command.refreshToken().isBlank()) {
			log.debug("[logout] Sin refresh token: nada que revocar");
			return;
		}

		RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHasher.hash(command.refreshToken()))
				.orElse(null);

		if (refreshToken == null) {
			log.debug("[logout] Refresh token inexistente: nada que revocar");
			return;
		}

		if (refreshToken.isRevoked()) {
			log.debug("[logout] Refresh token ya revocado del usuario {}", refreshToken.getUserId());
			return;
		}

		refreshToken.revocar();
		refreshTokenRepository.save(refreshToken);
		log.debug("[logout] Refresh token revocado del usuario {}", refreshToken.getUserId());
	}
}
