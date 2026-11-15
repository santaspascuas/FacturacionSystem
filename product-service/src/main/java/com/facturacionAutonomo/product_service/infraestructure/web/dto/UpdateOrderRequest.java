package com.facturacionAutonomo.product_service.infraestructure.web.dto;

import jakarta.validation.constraints.Min;

/**
 * Cuerpo de PUT /api/order/{publicId}: los datos del pedido que se pueden cambiar después de abrirlo.
 * Hoy solo el número de personas (opcional: en un pedido para llevar va null).
 */
public class UpdateOrderRequest {

	@Min(value = 1, message = "El número de personas tiene que ser al menos 1")
	private Integer numPersonas;

	public Integer getNumPersonas() {
		return numPersonas;
	}

	public void setNumPersonas(Integer numPersonas) {
		this.numPersonas = numPersonas;
	}

}
