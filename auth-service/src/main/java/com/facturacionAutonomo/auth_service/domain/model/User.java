package com.facturacionAutonomo.auth_service.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.facturacionAutonomo.auth_service.domain.exception.DatoInvalidoException;

public class User {
	public static final int MAX_INTENTOS_FALLIDOS = 3;

	private final UUID id;
	private final UUID tenantId;
	private final Email email;
	private final String passwordHash;
	private final Role role;
	private final LocalDateTime createdAt;
	private final boolean accountNonLocked;
	private final boolean accountNonExpired;
	private final int failedAttempts;
	private final LocalDateTime lockTime;

	private User(UUID id, UUID tenantId, Email email, String passwordHash, Role role, LocalDateTime createdAt,
			boolean accountNonLocked, boolean accountNonExpired, int failedAttempts, LocalDateTime lockTime) {
		this.id = id;
		this.tenantId = tenantId;
		this.email = email;
		this.passwordHash = passwordHash;
		this.role = role;
		this.createdAt = createdAt;
		this.accountNonLocked = accountNonLocked;
		this.accountNonExpired = accountNonExpired;
		this.failedAttempts = failedAttempts;
		this.lockTime = lockTime;
	}

	public static User crearUser(UUID tenantId, Email email, String passwordHash, Role role, LocalDateTime now) {
		if (tenantId == null || email == null || role == null || now == null) {
			throw new DatoInvalidoException("Faltan datos obligatorios del usuario");
		}
		if (passwordHash == null || passwordHash.isBlank()) {
			throw new DatoInvalidoException("La contraseña es obligatoria");
		}
		return new User(UUID.randomUUID(), tenantId, email, passwordHash, role, now, true, true, 0, null);
	}

	public static User restoreDataUser(UUID id, UUID tenantId, Email email, String passwordHash, Role role,
			LocalDateTime createdAt, boolean accountNonLocked, boolean accountNonExpired, int failedAttempts,
			LocalDateTime lockTime) {
		return new User(id, tenantId, email, passwordHash, role, createdAt, accountNonLocked, accountNonExpired,
				failedAttempts, lockTime);
	}

	public User registrarIntentoFallido(LocalDateTime now) {
		if (now == null) {
			throw new DatoInvalidoException("La fecha del intento es obligatoria");
		}
		int intentos = failedAttempts + 1;
		if (intentos >= MAX_INTENTOS_FALLIDOS) {
			return new User(id, tenantId, email, passwordHash, role, createdAt, false, accountNonExpired, intentos, now);
		}
		return new User(id, tenantId, email, passwordHash, role, createdAt, accountNonLocked, accountNonExpired,
				intentos, lockTime);
	}

	public User registrarLoginCorrecto() {
		if (failedAttempts == 0) {
			return this;
		}
		return new User(id, tenantId, email, passwordHash, role, createdAt, accountNonLocked, accountNonExpired, 0,
				lockTime);
	}

	public UUID getId() {
		return id;
	}

	public UUID getTenantId() {
		return tenantId;
	}

	public Email getEmail() {
		return email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public Role getRole() {
		return role;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	public int getFailedAttempts() {
		return failedAttempts;
	}

	public LocalDateTime getLockTime() {
		return lockTime;
	}
}
