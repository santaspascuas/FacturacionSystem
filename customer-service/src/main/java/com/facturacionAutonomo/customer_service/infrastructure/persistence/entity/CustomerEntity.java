package com.facturacionAutonomo.customer_service.infrastructure.persistence.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "customers")
public class CustomerEntity {

	@Id
	@Column(name = "customer_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_id_generator")
	@SequenceGenerator(name = "customer_id_generator", sequenceName = "customer_id_seq", allocationSize = 1)
	private Long id;

	@Column(name = "tenant_id", nullable = false, unique = true, updatable = false)
	private UUID tenantId;

	@Column(name = "razon_social", nullable = false, length = 120)
	private String razonSocial;

	@Column(nullable = false, length = 9)
	private String nif;
	
	@Column(length = 255)
	private String email;

	@OneToMany(mappedBy = "customer", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	private List<AddressEntity> addresses = new ArrayList<>();

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@Version
	private Long version;

	public CustomerEntity() {
		super();
	}

	/** Mantiene los dos lados de la relación sincronizados. */
	public void addAddress(AddressEntity address) {
		address.setCustomer(this);
		addresses.add(address);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UUID getTenantId() {
		return tenantId;
	}

	public void setTenantId(UUID tenantId) {
		this.tenantId = tenantId;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public String getNif() {
		return nif;
	}

	public void setNif(String nif) {
		this.nif = nif;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<AddressEntity> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<AddressEntity> addresses) {
		this.addresses = addresses;
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

	// Sin las direcciones: evita disparar la carga lazy al loguear.
	@Override
	public String toString() {
		return "CustomerEntity [id=" + id + ", tenantId=" + tenantId + ", razonSocial=" + razonSocial + ", nif="
				+ nif + ", email=" + email + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}
}
