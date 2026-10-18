package com.facturacionAutonomo.auth_service.application.service;

import java.time.Clock;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.auth_service.application.result.AccessToken;
import com.facturacionAutonomo.auth_service.application.command.RefreshSessionCommand;
import com.facturacionAutonomo.auth_service.application.config.RefreshTokenSettings;
import com.facturacionAutonomo.auth_service.application.port.in.RefreshSessionUseCase;
import com.facturacionAutonomo.auth_service.application.port.out.RefreshTokenGeneratorPort;
import com.facturacionAutonomo.auth_service.application.port.out.RefreshTokenRepositoryPort;
import com.facturacionAutonomo.auth_service.application.port.out.TokenHasherPort;
import com.facturacionAutonomo.auth_service.application.port.out.TokenProviderPort;
import com.facturacionAutonomo.auth_service.application.port.out.UserRepositoryPort;
import com.facturacionAutonomo.auth_service.application.result.AuthTokens;
import com.facturacionAutonomo.auth_service.domain.exception.CuentaBloqueadaException;
import com.facturacionAutonomo.auth_service.domain.exception.CuentaExpiradaException;
import com.facturacionAutonomo.auth_service.domain.exception.RefreshTokenInvalidoException;
import com.facturacionAutonomo.auth_service.domain.model.RefreshToken;
import com.facturacionAutonomo.auth_service.domain.model.User;

@Service
public class RefreshSessionService implements  RefreshSessionUseCase{
	private final UserRepositoryPort userRepositoryPort;
	private final TokenHasherPort tokenHasher;
	private final RefreshTokenSettings refreshTokenSettings;
	private final Clock clock;
	private final TokenProviderPort tokenprovider;
	private final RefreshTokenGeneratorPort refreshTokenGenerator;
	private final RefreshTokenRepositoryPort refreshTokenRepository;

	public RefreshSessionService(UserRepositoryPort userRepositoryPort, TokenHasherPort tokenHasher,
			RefreshTokenSettings refreshTokenSettings, Clock clock, TokenProviderPort tokenprovider,
			RefreshTokenGeneratorPort refreshTokenGenerator, RefreshTokenRepositoryPort refreshTokenRepository) {
		super();
		this.userRepositoryPort = userRepositoryPort;
		this.tokenHasher = tokenHasher;
		this.refreshTokenSettings = refreshTokenSettings;
		this.clock = clock;
		this.tokenprovider = tokenprovider;
		this.refreshTokenGenerator = refreshTokenGenerator;
		this.refreshTokenRepository = refreshTokenRepository;
	}

	private static final Logger log = LoggerFactory.getLogger(RefreshSessionService.class);

	@Override
	@Transactional
	public AuthTokens execute(RefreshSessionCommand command) {
		log.info("Estoy ejecutando:[AuthTokens execute] desde el service");

		if(command.refreshToken() == null || command.refreshToken().isBlank()) {
			throw new RefreshTokenInvalidoException();
		}
		String hasheado = tokenHasher.hash(command.refreshToken());

		RefreshToken encontrado = refreshTokenRepository.findByTokenHash(hasheado)
				.orElseThrow(()-> new RefreshTokenInvalidoException());

		log.debug("Recibimos el refresh del usuario:{} revocado:{} caduca:{}", encontrado.getUserId(),
				encontrado.isRevoked(), encontrado.getExpiresAt());

		User usuario = userRepositoryPort.findById(encontrado.getUserId())
				.orElseThrow(() -> new RefreshTokenInvalidoException());

		if (!usuario.isAccountNonLocked()) {
			log.debug("[refresh] Cuenta bloqueada: {}", usuario.getId());
			throw new CuentaBloqueadaException();
		}
		if (!usuario.isAccountNonExpired()) {
			log.debug("[refresh] Cuenta expirada: {}", usuario.getId());
			throw new CuentaExpiradaException();
		}

		LocalDateTime now = LocalDateTime.now(clock);
		String nuevoRaw = refreshTokenGenerator.generate();
		String nuevoHash = tokenHasher.hash(nuevoRaw);

		encontrado.renovar(nuevoHash, now, refreshTokenSettings.ttl());
		refreshTokenRepository.save(encontrado);
		log.debug("[refresh] Renovado para el usuario {}, caduca: {}", usuario.getId(), encontrado.getExpiresAt());

		AccessToken accessToken = tokenprovider.generarToken(usuario);

		return new AuthTokens(accessToken.value(), accessToken.expiresIn(), nuevoRaw);
	}
}
