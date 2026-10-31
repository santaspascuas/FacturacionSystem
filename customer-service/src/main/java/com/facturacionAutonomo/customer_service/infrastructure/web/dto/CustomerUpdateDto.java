package com.facturacionAutonomo.customer_service.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CustomerUpdateDto {
	
	@NotBlank(message = "La razón social es obligatoria")
	@Size(max = 120, message = "La razón social no puede superar 120 caracteres")
	private String razonSocial;
	
	
	@NotBlank(message = "El NIF es obligatorio")
	@Pattern(regexp = "^\\s*[A-Za-z0-9]{9}\\s*$", message = "El NIF debe tener 9 caracteres alfanuméricos")
	private String nif;


	public CustomerUpdateDto() {
		super();
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


	@Override
	public String toString() {
		return "CustomerUpdateDto [razonSocial=" + razonSocial + ", nif=" + nif + "]";
	}
	
	
	
	
	
	
	


}
