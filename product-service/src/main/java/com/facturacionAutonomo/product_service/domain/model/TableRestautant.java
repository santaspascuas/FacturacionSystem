package com.facturacionAutonomo.product_service.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.exception.TableException;

public class TableRestautant {
	private final Long id;
	private final UUID tenantId;
	private final UUID publicId;
	private int tableNumber;
	private int capacity;
	private TableZone zone;
	private TableStatus status;
	private final LocalDateTime createdAt;
	private LocalDateTime updatedAt;


	private TableRestautant(Long id, UUID tenantId, UUID publicId, int tableNumber, int capacity, TableZone zone,
			TableStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.id = id;
		this.tenantId = tenantId;
		this.publicId = publicId;
		this.tableNumber = tableNumber;
		this.capacity = capacity;
		this.zone = zone;
		this.status = status;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}


	public static TableRestautant crearTable(UUID tenantId, Integer tableNumber, Integer capacity, TableZone zone,
			LocalDateTime now) {
		return new TableRestautant(null, tenantId, UUID.randomUUID(), numeroValido(tableNumber),
				capacidadValida(capacity), zonaObligatoria(zone), TableStatus.DISPONIBLE, now, now);
	}

	public static TableRestautant restoreData(Long id, UUID tenantId, UUID publicId, int tableNumber, int capacity,
			TableZone zone, TableStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
		return new TableRestautant(id, tenantId, publicId, tableNumber, capacity, zone, status, createdAt, updatedAt);
	}


	public static int numeroValido(Integer tableNumber) {
		if(tableNumber == null || tableNumber < 1) {
			throw new TableException("El número de mesa tiene que ser al menos 1");
		}
		return tableNumber;
	}

	public static int capacidadValida(Integer capacity) {
		if(capacity == null || capacity < 1) {
			throw new TableException("La capacidad de la mesa tiene que ser al menos 1");
		}
		return capacity;
	}

	public static TableZone zonaObligatoria(TableZone zone) {
		if(zone == null) {
			throw new TableException("La zona de la mesa es obligatoria");
		}
		return zone;
	}

	public static TableStatus estadoObligatorio(TableStatus status) {
		if(status == null) {
			throw new TableException("El estado de la mesa es obligatorio");
		}
		return status;
	}


	public void updateDetails(Integer tableNumber, Integer capacity, TableZone zone, LocalDateTime updatedAt) {
		this.tableNumber = numeroValido(tableNumber);
		this.capacity = capacidadValida(capacity);
		this.zone = zonaObligatoria(zone);
		this.updatedAt = updatedAt;
	}

	public void cambiarEstado(TableStatus status, LocalDateTime updatedAt) {
		this.status = estadoObligatorio(status);
		this.updatedAt = updatedAt;
	}


	public Long getId() {
		return id;
	}
	public UUID getTenantId() {
		return tenantId;
	}
	public UUID getPublicId() {
		return publicId;
	}
	public int getTableNumber() {
		return tableNumber;
	}
	public int getCapacity() {
		return capacity;
	}
	public TableZone getZone() {
		return zone;
	}
	public TableStatus getStatus() {
		return status;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	@Override
	public String toString() {
		return "TableRestautant [id=" + id + ", tenantId=" + tenantId + ", publicId=" + publicId + ", tableNumber="
				+ tableNumber + ", capacity=" + capacity + ", zone=" + zone + ", status=" + status + ", createdAt="
				+ createdAt + ", updatedAt=" + updatedAt + "]";
	}

}
