package com.facturacionAutonomo.auth_service.application.service;

import java.time.Clock;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.auth_service.application.command.RegisterUserCommand;
import com.facturacionAutonomo.auth_service.application.result.RegisterUserResult;
import com.facturacionAutonomo.auth_service.application.port.in.RegisterUserUseCase;
import com.facturacionAutonomo.auth_service.application.port.out.PasswordHasherPort;
import com.facturacionAutonomo.auth_service.application.port.out.TenantRepositoryPort;
import com.facturacionAutonomo.auth_service.application.port.out.UserRepositoryPort;
import com.facturacionAutonomo.auth_service.domain.exception.DatoInvalidoException;
import com.facturacionAutonomo.auth_service.domain.exception.EmailYaRegistradoException;
import com.facturacionAutonomo.auth_service.domain.model.Email;
import com.facturacionAutonomo.auth_service.domain.model.Role;
import com.facturacionAutonomo.auth_service.domain.model.Tenant;
import com.facturacionAutonomo.auth_service.domain.model.User;

@Service
public class RegisterUserService implements RegisterUserUseCase{
	private static final int LONGITUD_MINIMA_PASSWORD = 8;

	private final UserRepositoryPort userRepositoryPort;
	private final TenantRepositoryPort tenantRepositoryPort;
	private final PasswordHasherPort passwordHasher;
	private final Clock clockea;

	private static final Logger log = LoggerFactory.getLogger(RegisterUserService.class);

	public RegisterUserService(UserRepositoryPort userRepositoryPort, TenantRepositoryPort tenantRepositoryPort,
			PasswordHasherPort passwordHasher, Clock clockea) {
		super();
		this.userRepositoryPort = userRepositoryPort;
		this.tenantRepositoryPort = tenantRepositoryPort;
		this.passwordHasher = passwordHasher;
		this.clockea = clockea;
	}

	@Override
	@Transactional
	public RegisterUserResult execute(RegisterUserCommand command) {
		log.info("Estoy ejecutando:[String execute] desde el service-- :{}", command.email());

		Email email = new Email(command.email());
		validarPassword(command.password());
		log.debug("El valor del email controlado:{}", email);

		if (userRepositoryPort.existsByEmail(email)) {
			log.warn("Entramos en la validacion del email");
			throw new EmailYaRegistradoException();
		}

		LocalDateTime now = LocalDateTime.now(clockea);
		log.debug("el valor: {}", now);

		Tenant tenant = Tenant.crearTenantPorDefecto(now);

		String hash = passwordHasher.hash(command.password());

		User usuario = User.crearUser(tenant.getId(), email, hash, Role.USER, now);

		Tenant tenantGuardado = tenantRepositoryPort.save(tenant);
		log.debug("[Tenant tenantRegistro :{}]", tenantGuardado);

		User guardado = userRepositoryPort.save(usuario);

		return new RegisterUserResult(guardado.getId(), guardado.getTenantId(), guardado.getRole().name());
	}

	private void validarPassword(String password) {
		if (password == null || password.length() < LONGITUD_MINIMA_PASSWORD) {
			throw new DatoInvalidoException("La contraseña debe tener al menos " + LONGITUD_MINIMA_PASSWORD + " caracteres");
		}
	}
}
