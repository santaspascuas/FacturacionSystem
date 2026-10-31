package com.facturacionAutonomo.customer_service.domain.model;

import java.util.UUID;

/**
 * Dirección de un customer. Su identidad es publicId.
 * active = dirección fiscal: como máximo una por customer (índice uk_addresses_customer_fiscal, V4).
 */
public class Address {
	private final UUID publicId;
	private final String calle;
	private final String postalCode;
	private final String city;
	private final String province;
	private final String country;
	private final boolean active;

	private Address(UUID publicId, String calle, String postalCode, String city, String province, String country,
			boolean active) {
		this.publicId = publicId;
		this.calle = calle;
		this.postalCode = postalCode;
		this.city = city;
		this.province = province;
		this.country = country;
		this.active = active;
	}

	public static Address crearAddress(String calle, String postalCode, String city, String province,
			String country) {
		return new Address(UUID.randomUUID(), calle, postalCode, city, province, country, true);
	}

	public static Address restoreData(UUID publicId, String calle, String postalCode, String city, String province,
			String country, boolean active) {
		return new Address(publicId, calle, postalCode, city, province, country, active);
	}

	/** Devuelve una dirección nueva con los datos cambiados. Conserva publicId y active. */
	public Address actualizarDatos(String calle, String postalCode, String city, String province, String country) {
		return new Address(publicId, calle, postalCode, city, province, country, active);
	}

	/** Devuelve la misma dirección marcada como fiscal. Desactivar la anterior es cosa del servicio. */
	public Address activar() {
		return new Address(publicId, calle, postalCode, city, province, country, true);
	}

	public UUID getPublicId() {
		return publicId;
	}

	public String getCalle() {
		return calle;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public String getCity() {
		return city;
	}

	public String getProvince() {
		return province;
	}

	public String getCountry() {
		return country;
	}

	public boolean isActive() {
		return active;
	}

	@Override
	public String toString() {
		return "Address [publicId=" + publicId + ", calle=" + calle + ", postalCode=" + postalCode + ", city=" + city
				+ ", active=" + active + "]";
	}
}
