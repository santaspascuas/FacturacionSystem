package com.facturacionAutonomo.product_service.infraestructure.persistence.entitys;

import java.time.LocalDateTime;
import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.TableStatus;
import com.facturacionAutonomo.product_service.domain.model.TableZone;

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
 * Mesa de un restaurante. Cada tenant tiene que estar para que los restaurantes no vean los datos de los demás:
 * el número de mesa solo es único dentro de cada tenant.
 * Las restricciones reales viven en la migración de Flyway; aquí solo se documentan (ddl-auto=validate).
 */
@Entity
@Table(name = "tablerestaurante", uniqueConstraints = @UniqueConstraint(name = "uk_tablerestaurante_tenant_number", columnNames = {
		"tenant_id", "table_number" }))
public class TablesEntity {

	@Id
	@Column(name = "table_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "table_id_generator")
	@SequenceGenerator(name = "table_id_generator", sequenceName = "table_id_seq", allocationSize = 1)
	private Long tableId;

	// public_id como en el resto de tablas: es el identificador que sale fuera del servicio.
	@Column(name = "public_id", nullable = false, unique = true, updatable = false)
	private UUID publicId;

	@Column(name = "tenant_id", nullable = false, updatable = false)
	private UUID tenantId;

	// El campo Java va en camelCase: Spring Data deduce de él los métodos (existsByTenantIdAndTableNumber).
	// El nombre de la columna se indica aparte.
	@Column(name = "table_number", nullable = false)
	private int tableNumber;

	@Column(nullable = false)
	private int capacity;

	// STRING guarda el nombre del enum ("TERRAZA", "DISPONIBLE"), que es lo que admiten los CHECK.
	// Sin @Enumerated se guardaría su posición (0, 1, 2...).
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private TableZone zone;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private TableStatus status;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Version
	private Long version;

	public TablesEntity() {
		super();
	}

	public Long getTableId() {
		return tableId;
	}

	public void setTableId(Long tableId) {
		this.tableId = tableId;
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

	public int getTableNumber() {
		return tableNumber;
	}

	public void setTableNumber(int tableNumber) {
		this.tableNumber = tableNumber;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public TableZone getZone() {
		return zone;
	}

	public void setZone(TableZone zone) {
		this.zone = zone;
	}

	public TableStatus getStatus() {
		return status;
	}

	public void setStatus(TableStatus status) {
		this.status = status;
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
