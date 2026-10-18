package com.facturacionAutonomo.auth_service.domain.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import com.facturacionAutonomo.auth_service.domain.exception.DatoInvalidoException;
import com.facturacionAutonomo.auth_service.domain.exception.RefreshTokenInvalidoException;

public class RefreshToken {
	private final UUID userId;
	private String tokenHash;
	private LocalDateTime expiresAt;
	private LocalDateTime createdAt;
	private boolean revoked;

	private RefreshToken(UUID userId, String tokenHash, LocalDateTime expiresAt, LocalDateTime createdAt, boolean revoked) {
		this.userId = userId;
		this.tokenHash = tokenHash;
		this.expiresAt = expiresAt;
		this.createdAt = createdAt;
		this.revoked = revoked;
	}

	public static RefreshToken crear(UUID userId, String tokenHash, LocalDateTime now, Duration duracion) {
		if (userId == null) {
			throw new DatoInvalidoException("El usuario es obligatorio");
		}
		validarEmision(tokenHash, now, duracion);
		return new RefreshToken(userId, tokenHash, now.plus(duracion), now, false);
	}

	public static RefreshToken restoreData(UUID userId, String tokenHash, LocalDateTime expiresAt, LocalDateTime createdAt, boolean revoked) {
		return new RefreshToken(userId, tokenHash, expiresAt, createdAt, revoked);
	}

	public void renovar(String nuevoTokenHash, LocalDateTime now, Duration duracion) {
		if (now == null || !estaActivo(now)) {
			throw new RefreshTokenInvalidoException();
		}
		emitir(nuevoTokenHash, now, duracion);
	}

	public void reemitir(String nuevoTokenHash, LocalDateTime now, Duration duracion) {
		emitir(nuevoTokenHash, now, duracion);
	}

	private void emitir(String nuevoTokenHash, LocalDateTime now, Duration duracion) {
		validarEmision(nuevoTokenHash, now, duracion);
		this.tokenHash = nuevoTokenHash;
		this.createdAt = now;
		this.expiresAt = now.plus(duracion);
		this.revoked = false;
	}

	public void revocar() {
		this.revoked = true;
	}

	public boolean estaActivo(LocalDateTime now) {
		return !revoked && now.isBefore(expiresAt);
	}

	private static void validarEmision(String tokenHash, LocalDateTime now, Duration duracion) {
		if (tokenHash == null || tokenHash.isBlank()) {
			throw new DatoInvalidoException("El hash del token es obligatorio");
		}
		if (now == null) {
			throw new DatoInvalidoException("La fecha de emisión es obligatoria");
		}
		if (duracion == null || duracion.isNegative() || duracion.isZero()) {
			throw new DatoInvalidoException("La duración del token debe ser positiva");
		}
	}

	public UUID getUserId() {
		return userId;
	}

	public String getTokenHash() {
		return tokenHash;
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public boolean isRevoked() {
		return revoked;
	}

	@Override
	public String toString() {
		return "RefreshToken [userId=" + userId + ", tokenHash=" + tokenHash + ", expiresAt=" + expiresAt
				+ ", createdAt=" + createdAt + ", revoked=" + revoked + "]";
	}
}
