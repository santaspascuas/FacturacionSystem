package com.facturacionAutonomo.product_service.infraestructure.persistence.entitys;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

/**
 * Cada tenant tiene sus categorías. El nombre es único dentro de cada tenant, no en toda la tabla.
 * Las restricciones reales viven en la migración de Flyway; aquí solo se documentan (ddl-auto=validate).
 */
@Entity
@Table(name = "menu_categories", uniqueConstraints = @UniqueConstraint(name = "uk_menu_categories_tenant_name", columnNames = {
		"tenant_id", "name" }))
public class MenuCategoryEntity {
	
	@Id
	@Column(name = "category_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_id_generator")
	@SequenceGenerator(name = "category_id_generator", sequenceName = "category_id_seq", allocationSize = 1)
	private Long categoryId;
	
	@Column(name = "public_id", nullable = false, unique = true, updatable = false)
	private UUID publicId;

	@Column(name = "tenant_id", nullable = false, updatable = false)
	private UUID tenantId;
	
	@Column(nullable = false, length = 100)
	private String name;
	
	@Column(nullable = false)
    private Integer displayOrder;

    @Column(nullable = false)
    private Boolean active = true;
    
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;
	
	
	@Version
	private Long version;
	

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public UUID getPublicId() {
		return publicId;
	}

	public void setPublicId(UUID publicId) {
		this.publicId = publicId;
	}

	public UUID getTenantId() {
		return tenantId;
	}

	public void setTenantId(UUID tenantId) {
		this.tenantId = tenantId;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	
	
	
	
	
	
	
	
	
	

}
