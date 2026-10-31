package com.facturacionAutonomo.customer_service.infrastructure.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo de la petición de alta del customer. Las direcciones se añaden después.
 */
public class CustomerResponseDto {

	@NotBlank(message = "La razón social es obligatoria")
	@Size(max = 120, message = "La razón social no puede superar 120 caracteres")
	private String razonSocial;

	@NotBlank(message = "El NIF es obligatorio")
	@Pattern(regexp = "^\\s*[A-Za-z0-9]{9}\\s*$", message = "El NIF debe tener 9 caracteres alfanuméricos")
	private String nif;

	@Email(message = "Formato de email inválido")
	@Size(max = 255, message = "El email no puede superar 255 caracteres")
	private String email;

	public CustomerResponseDto() {
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "CustomerResponseDto [razonSocial=" + razonSocial + ", nif=" + nif + ", email=" + email + "]";
	}
}
