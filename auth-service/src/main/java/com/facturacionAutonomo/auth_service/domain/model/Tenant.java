package com.facturacionAutonomo.auth_service.domain.model;

import com.facturacionAutonomo.auth_service.domain.exception.DatoInvalidoException;

import java.time.LocalDateTime;
import java.util.UUID;

public class Tenant {
	private final UUID id;
	private final String name;
	private final LocalDateTime createdAt;

	private Tenant(UUID id, String name, LocalDateTime createdAt) {
		this.id = id;
		this.name = name;
		this.createdAt = createdAt;
	}

	public static Tenant crearTenant(String name, LocalDateTime now) {
		validarFecha(now);
		if (name == null || name.isBlank()) {
			throw new DatoInvalidoException("El nombre del tenant es obligatorio");
		}
		return new Tenant(UUID.randomUUID(), name.trim(), now);
	}

	public static Tenant crearTenantPorDefecto(LocalDateTime now) {
		validarFecha(now);
		UUID id = UUID.randomUUID();
		return new Tenant(id, "tenant_" + id, now);
	}

	private static void validarFecha(LocalDateTime now) {
		if (now == null) {
			throw new DatoInvalidoException("La fecha de creación es obligatoria");
		}
	}

	public static Tenant restoreData(UUID id, String name, LocalDateTime createdAt) {
		return new Tenant(id, name, createdAt);
	}

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	@Override
	public String toString() {
		return "Tenant [id=" + id + ", name=" + name + ", createdAt=" + createdAt + "]";
	}
}
