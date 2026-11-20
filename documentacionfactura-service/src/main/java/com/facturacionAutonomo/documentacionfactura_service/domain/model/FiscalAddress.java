package com.facturacionAutonomo.documentacionfactura_service.domain.model;

/** Domicilio fiscal del restaurante tal como sale en el ticket. Inmutable. */
public class FiscalAddress {
	private final String calle;
	private final String postalCode;
	private final String city;
	private final String province;
	private final String country;

	private FiscalAddress(String calle, String postalCode, String city, String province, String country) {
		this.calle = calle;
		this.postalCode = postalCode;
		this.city = city;
		this.province = province;
		this.country = country;
	}

	public static FiscalAddress restoreData(String calle, String postalCode, String city, String province,
			String country) {
		return new FiscalAddress(calle, postalCode, city, province, country);
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
		return "FiscalAddress [calle=" + calle + ", postalCode=" + postalCode + ", city=" + city + ", province="
				+ province + ", country=" + country + "]";
	}
}
