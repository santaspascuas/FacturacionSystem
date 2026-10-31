package com.facturacionAutonomo.customer_service.domain.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Datos fiscales del tenant como emisor de facturas. Uno por tenant: el tenantId es su identidad.
 */
public class Customer {
	private final UUID tenantId;
	private final String razonSocial;
	private final String nif;
	private final String email;
	private final List<Address> addresses;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	private Customer(UUID tenantId, String razonSocial, String nif, String email, List<Address> addresses,
			LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.tenantId = tenantId;
		this.razonSocial = razonSocial;
		this.nif = nif;
		this.email = email;
		this.addresses = addresses == null ? List.of() : List.copyOf(addresses);
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public static Customer crearCustomer(UUID tenantId, String razonSocial, String nif, String email,
			LocalDateTime now) {
		// Un customer recién creado no tiene direcciones: se añaden después por su propio repositorio.
		return new Customer(tenantId, razonSocial, nif, email, List.of(), now, now);
	}

	public static Customer restoreData(UUID tenantId, String razonSocial, String nif, String email,
			List<Address> addresses, LocalDateTime createdAt, LocalDateTime updatedAt) {
		return new Customer(tenantId, razonSocial, nif, email, addresses, createdAt, updatedAt);
	}

	/**
	 * Actualiza solo la razón social y el NIF. Conserva tenant, email, direcciones y createdAt;
	 * updatedAt pasa a ser now.
	 */
	public Customer actualizarDatos(String razonSocial, String nif, LocalDateTime now) {
		return new Customer(tenantId, razonSocial, nif, email, addresses, createdAt, now);
	}

	public UUID getTenantId() {
		return tenantId;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public String getNif() {
		return nif;
	}

	public String getEmail() {
		return email;
	}

	/** Lista inmutable. */
	public List<Address> getAddresses() {
		return addresses;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	@Override
	public String toString() {
		return "Customer [tenantId=" + tenantId + ", razonSocial=" + razonSocial + ", nif=" + nif
				+ ", addresses=" + addresses.size() + "]";
	}
}
