package com.facturacionAutonomo.product_service.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Usuario del TPV de un tenant. Un tenant tiene N usuarios: la identidad es el id, no el tenantId.
 */
public class UsersTpv {
	private final Long id;
	private final UUID tenantId;
	private final String username;
	private final String password_hash;
	private final Role roletpv;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	private UsersTpv(Long id, UUID tenantId, String username, String password_hash, Role roletpv,
			LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.id = id;
		this.tenantId = tenantId;
		this.username = username;
		this.password_hash = password_hash;
		this.roletpv = roletpv;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}


	public static UsersTpv crearUsertpv(UUID tenantId, String username, String password_hash, Role roletpv,
			LocalDateTime now) {
		return new UsersTpv(null, tenantId, username, password_hash, roletpv, now, now);
	}

	public static UsersTpv restoreData(Long id, UUID tenantId, String username, String password_hash, Role roletpv,
			LocalDateTime createdAt, LocalDateTime updatedAt) {
		return new UsersTpv(id, tenantId, username, password_hash, roletpv, createdAt, updatedAt);
	}

	public Long getId() {
		return id;
	}

	public UUID getTenantId() {
		return tenantId;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword_hash() {
		return password_hash;
	}

	public Role getRoletpv() {
		return roletpv;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	@Override
	public String toString() {
		return "UsersTpv [id=" + id + ", tenantId=" + tenantId + ", username=" + username + ", roletpv=" + roletpv
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}
}
