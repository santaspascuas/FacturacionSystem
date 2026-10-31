package com.facturacionAutonomo.customer_service.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Los límites coinciden con las columnas de la tabla addresses: si se superan, el error llegaría desde la BD.
public class AddressRequestDto {
	@NotBlank(message = "La calle es obligatoria")
	@Size(max = 120, message = "La calle no puede superar los 120 caracteres")
	private String calle;

	@NotBlank(message = "El código postal es obligatorio")
	@Size(max = 10, message = "El código postal no puede superar los 10 caracteres")
	private String postalCode;

	@NotBlank(message = "La ciudad es obligatoria")
	@Size(max = 100, message = "La ciudad no puede superar los 100 caracteres")
	private String city;

	@NotBlank(message = "La provincia es obligatoria")
	@Size(max = 100, message = "La provincia no puede superar los 100 caracteres")
	private String province;

	@NotBlank(message = "El pais es obligatorio")
	@Pattern(regexp = "^[A-Z]{2}$", message = "El pais debe ser un código ISO de 2 letras en mayúsculas (ej. ES)")
	private String country;

	public AddressRequestDto(String calle, String postalCode, String city, String province, String country) {
		super();
		this.calle = calle;
		this.postalCode = postalCode;
		this.city = city;
		this.province = province;
		this.country = country;
	}
	
	public AddressRequestDto() {}

	public String getCalle() {
		return calle;
	}

	public void setCalle(String calle) {
		this.calle = calle;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}
