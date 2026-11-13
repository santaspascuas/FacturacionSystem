package com.facturacionAutonomo.product_service.infraestructure.persistence.entitys;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.ModifierType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

/**
 * Modificador de un tenant ("Sin cebolla", "Extra de queso"). El nombre es único dentro de cada tenant.
 * Las restricciones reales viven en la migración de Flyway; aquí solo se documentan (ddl-auto=validate).
 */
@Entity
@Table(name = "modifiers", uniqueConstraints = @UniqueConstraint(name = "uk_modifiers_tenant_name", columnNames = {
		"tenant_id", "name" }))
public class ModifierEntity {

	@Id
	@Column(name = "modificador_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "modificador_id_generator")
	@SequenceGenerator(name = "modificador_id_generator", sequenceName = "modificador_id_seq", allocationSize = 1)
	private Long modificadorId;

	@Column(name = "public_id", nullable = false, unique = true, updatable = false)
	private UUID publicId;

	@Column(name = "tenant_id", nullable = false, updatable = false)
	private UUID tenantId;

	@Column(nullable = false, length = 100)
	private String name;

	// STRING guarda el nombre del enum ("ANADIR"), que es lo que admite el CHECK.
	@Enumerated(EnumType.STRING)
	@Column(name = "modifier_type", nullable = false, length = 20)
	private ModifierType modifierType;

	@Column(name = "price_delta", nullable = false, precision = 10, scale = 2)
	private BigDecimal priceDelta;

	@Column(nullable = false)
	private Boolean active = true;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Version
	private Long version;

	public ModifierEntity() {
		super();
	}

	public Long getModificadorId() {
		return modificadorId;
	}

	public void setModificadorId(Long modificadorId) {
		this.modificadorId = modificadorId;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ModifierType getModifierType() {
		return modifierType;
	}

	public void setModifierType(ModifierType modifierType) {
		this.modifierType = modifierType;
	}

	public BigDecimal getPriceDelta() {
		return priceDelta;
	}

	public void setPriceDelta(BigDecimal priceDelta) {
		this.priceDelta = priceDelta;
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

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

}
