package com.facturacionAutonomo.product_service.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.exception.CategoryException;

/**
 * Categoría de la carta de un tenant (restaurante). El nombre solo es único dentro de cada tenant.
 */
public class MenuCategory {
	private final Long id;
	private final UUID tenantId;
	private final UUID publicId;
	private String name;
	private Integer displayOrder;
	private boolean active;
	private final LocalDateTime createdAt;
	private LocalDateTime updatedAt;


	private MenuCategory(Long id, UUID tenantId, UUID publicId, String name, Integer displayOrder, boolean active,
			LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.id = id;
		this.tenantId = tenantId;
		this.publicId = publicId;
		this.name = name;
		this.displayOrder = displayOrder;
		this.active = active;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}


	// Una categoría nueva nace activa y con su UUID público, que es el que se expone fuera del servicio.
	public static MenuCategory crearCategory(UUID tenantId, String name, Integer displayOrder, LocalDateTime now) {
		return new MenuCategory(null, tenantId, UUID.randomUUID(), name, displayOrder, true, now, now);
	}

	public static MenuCategory restoreData(Long id, UUID tenantId, UUID publicId, String name, Integer displayOrder,
			boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
		return new MenuCategory(id, tenantId, publicId, name, displayOrder, active, createdAt, updatedAt);
	}
	
	// Sin espacios alrededor: "Bebidas " no debe ser una categoría distinta de "Bebidas".
	// Lo usan el alta y la modificación para comprobar que el nombre está libre con el mismo valor que se guardará.
	public static String normalizarNombre(String name) {
		if(name == null || name.isBlank()) {
			throw new CategoryException("El nombre de la categoría es obligatorio");
		}
		return name.trim();
	}

	public void updateDetails(String name, boolean active, LocalDateTime updatedAt) {
		this.name=name;
		this.active=active;
		this.updatedAt=updatedAt;
		
	}


	public Long getId() {
		return id;
	}
	public UUID getTenantId() {
		return tenantId;
	}
	public String getName() {
		return name;
	}
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	public UUID getPublicId() {
		return publicId;
	}
	public boolean isActive() {
		return active;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	@Override
	public String toString() {
		return "MenuCategory [id=" + id + ", tenantId=" + tenantId + ", publicId=" + publicId + ", name=" + name
				+ ", displayOrder=" + displayOrder + ", active=" + active + ", createdAt=" + createdAt
				+ ", updatedAt=" + updatedAt + "]";
	}



}
