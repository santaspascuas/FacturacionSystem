package com.facturacionAutonomo.documentacionfactura_service.application.result;

import com.facturacionAutonomo.documentacionfactura_service.domain.model.FiscalAddress;

/**
 * Dirección fiscal del restaurante tal como sale por la API de este servicio (dentro de CustomerResult).
 * Es la que se imprime en el ticket: la dirección con active = true en customer-service.
 */
public class Address {
	private final String calle;
	private final String postalCode;
	private final String city;
	private final String province;
	private final String country;

	private Address(String calle, String postalCode, String city, String province, String country) {
		this.calle = calle;
		this.postalCode = postalCode;
		this.city = city;
		this.province = province;
		this.country = country;
	}

	public static Address from(FiscalAddress direccion) {
		return new Address(direccion.getCalle(), direccion.getPostalCode(), direccion.getCity(),
				direccion.getProvince(), direccion.getCountry());
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

	@Override
	public String toString() {
		return "Address [calle=" + calle + ", postalCode=" + postalCode + ", city=" + city + ", province=" + province
				+ ", country=" + country + "]";
	}
}
