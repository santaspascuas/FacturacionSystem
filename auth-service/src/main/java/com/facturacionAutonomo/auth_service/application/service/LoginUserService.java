package com.facturacionAutonomo.auth_service.application.service;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.facturacionAutonomo.auth_service.application.result.AccessToken;
import com.facturacionAutonomo.auth_service.application.command.LoginCommand;
import com.facturacionAutonomo.auth_service.application.config.RefreshTokenSettings;
import com.facturacionAutonomo.auth_service.application.port.in.LoginUserUseCase;
import com.facturacionAutonomo.auth_service.application.port.out.PasswordHasherPort;
import com.facturacionAutonomo.auth_service.application.port.out.RefreshTokenGeneratorPort;
import com.facturacionAutonomo.auth_service.application.port.out.RefreshTokenRepositoryPort;
import com.facturacionAutonomo.auth_service.application.port.out.TokenHasherPort;
import com.facturacionAutonomo.auth_service.application.port.out.TokenProviderPort;
import com.facturacionAutonomo.auth_service.application.port.out.UserRepositoryPort;
import com.facturacionAutonomo.auth_service.application.result.AuthTokens;
import com.facturacionAutonomo.auth_service.domain.exception.CredencialesInvalidasException;
import com.facturacionAutonomo.auth_service.domain.exception.CuentaBloqueadaException;
import com.facturacionAutonomo.auth_service.domain.exception.CuentaExpiradaException;
import com.facturacionAutonomo.auth_service.domain.exception.DatoInvalidoException;
import com.facturacionAutonomo.auth_service.domain.model.Email;
import com.facturacionAutonomo.auth_service.domain.model.RefreshToken;
import com.facturacionAutonomo.auth_service.domain.model.User;

@Service
public class LoginUserService implements LoginUserUseCase{
	private static final String PASSWORD_FICTICIA = "password-ficticia-para-igualar-tiempos";

	private final UserRepositoryPort userRepositoryPort;

	private final PasswordHasherPort passwordhashear;

	private final TokenProviderPort tokenprovider;

	private final RefreshTokenRepositoryPort refreshTokenRepository;

	private final RefreshTokenGeneratorPort refreshTokenGenerator;

	private final TokenHasherPort tokenHasher;

	private final RefreshTokenSettings refreshTokenSettings;

	private final Clock clock;

	private final String hashFicticio;

	private static final Logger log = LoggerFactory.getLogger(LoginUserService.class);

	public LoginUserService(UserRepositoryPort userRepositoryPort, PasswordHasherPort passwordhashear,
			TokenProviderPort tokenprovider, RefreshTokenRepositoryPort refreshTokenRepository,
			RefreshTokenGeneratorPort refreshTokenGenerator, TokenHasherPort tokenHasher,
			RefreshTokenSettings refreshTokenSettings, Clock clock) {
		this.userRepositoryPort = userRepositoryPort;
		this.passwordhashear = passwordhashear;
		this.tokenprovider = tokenprovider;
		this.refreshTokenRepository = refreshTokenRepository;
		this.refreshTokenGenerator = refreshTokenGenerator;
		this.tokenHasher = tokenHasher;
		this.refreshTokenSettings = refreshTokenSettings;
		this.clock = clock;
		this.hashFicticio = passwordhashear.hash(PASSWORD_FICTICIA);
	}

	@Override
	public AuthTokens execute(LoginCommand command) {
		log.info("[Ejecutamos el loguin] :{}", command.email());

		Email email;
		try {
			email = new Email(command.email());
		} catch (DatoInvalidoException ex) {
			throw new CredencialesInvalidasException();
		}

		User usuario = userRepositoryPort.findByEmail(email).orElse(null);
		if (usuario == null) {
			log.debug("[login] Email no registrado");
			passwordhashear.matches(passwordOVacia(command.password()), hashFicticio);
			throw new CredencialesInvalidasException();
		}

		log.debug("usuario encontrado:{}", usuario.getEmail());

		if (!usuario.isAccountNonLocked()) {
			log.debug("[login] Cuenta bloqueada: {}", usuario.getId());
			throw new CuentaBloqueadaException();
		}

		if (!usuario.isAccountNonExpired()) {
			log.debug("[login] Cuenta expirada: {}", usuario.getId());
			throw new CuentaExpiradaException();
		}

		if (!passwordhashear.matches(passwordOVacia(command.password()), usuario.getPasswordHash())) {
			User conIntento = usuario.registrarIntentoFallido(LocalDateTime.now(clock));
			userRepositoryPort.save(conIntento);
			log.debug("[login] Contraseña incorrecta para el usuario: {} (intentos: {})", usuario.getId(),
					conIntento.getFailedAttempts());

			if (!conIntento.isAccountNonLocked()) {
				log.warn("[login] Cuenta bloqueada tras {} intentos fallidos: {}", conIntento.getFailedAttempts(),
						usuario.getId());
				throw new CuentaBloqueadaException();
			}
			throw new CredencialesInvalidasException();
		}

		if (usuario.getFailedAttempts() > 0) {
			usuario = userRepositoryPort.save(usuario.registrarLoginCorrecto());
			log.debug("[login] Contador de intentos fallidos reiniciado: {}", usuario.getId());
		}

		String refreshTokenRaw = emitirRefreshToken(usuario);

		AccessToken accessToken = tokenprovider.generarToken(usuario);
		log.debug("Generado el access token para el usuario:{}", usuario.getId());

		return new AuthTokens(accessToken.value(), accessToken.expiresIn(), refreshTokenRaw);
	}

	private String emitirRefreshToken(User usuario) {
		log.info("[emitirRefreshToken] usuario: {}", usuario.getId());

		String raw = refreshTokenGenerator.generate();
		String hash = tokenHasher.hash(raw);
		LocalDateTime now = LocalDateTime.now(clock);
		Duration ttl = refreshTokenSettings.ttl();

		RefreshToken refreshToken = refreshTokenRepository.findByUserId(usuario.getId()).orElse(null);

		if (refreshToken == null) {
			refreshToken = RefreshToken.crear(usuario.getId(), hash, now, ttl);
			log.debug("[emitirRefreshToken] Primer refresh token del usuario -> se crea");
		} else {
			refreshToken.reemitir(hash, now, ttl);
			log.debug("[emitirRefreshToken] El usuario ya tenia refresh token -> se reemite");
		}

		refreshTokenRepository.save(refreshToken);
		log.debug("[emitirRefreshToken] Refresh token guardado, caduca: {}", refreshToken.getExpiresAt());
		return raw;
	}

	private static String passwordOVacia(String password) {
		return password == null ? "" : password;
	}
}
