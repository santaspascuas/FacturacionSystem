package com.facturacionAutonomo.product_service.infraestructure.web.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo de POST /api/order/{publicId}/items: los platos que se añaden a un pedido abierto.
 * Cada línea tiene el mismo formato y las mismas validaciones que en el alta del pedido.
 */
public class AnadirLineasOrderRequest {

	// Opcional. El TPV genera una por envío (p. ej. un UUID) y la repite si reintenta ese mismo envío.
	@Size(max = 100, message = "La clave de idempotencia no puede superar los 100 caracteres")
	private String idempotencyKey;

	// @NotEmpty: al menos una línea. @Valid: valida también cada línea por dentro.
	@NotEmpty(message = "Hay que añadir al menos una línea")
	private List<@Valid LineaOrderRequest> lineas;

	public String getIdempotencyKey() {
		return idempotencyKey;
	}

	public void setIdempotencyKey(String idempotencyKey) {
		this.idempotencyKey = idempotencyKey;
	}

	public List<LineaOrderRequest> getLineas() {
		return lineas;
	}

	public void setLineas(List<LineaOrderRequest> lineas) {
		this.lineas = lineas;
	}

}
