package com.facturacionAutonomo.product_service.infraestructure.web.dto;

import com.facturacionAutonomo.product_service.domain.model.OrderStatus;

import jakarta.validation.constraints.NotNull;

/**
 * Cuerpo del cambio de estado de un pedido (PATCH /api/order/{publicId}/status).
 * Un valor que no exista en OrderStatus no se puede leer y se responde 400.
 */
public class CambiarEstadoOrderRequest {

	@NotNull(message = "El estado del pedido es obligatorio")
	private OrderStatus status;

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

}
