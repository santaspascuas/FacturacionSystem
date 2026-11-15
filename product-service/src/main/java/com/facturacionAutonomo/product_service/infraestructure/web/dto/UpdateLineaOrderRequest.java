package com.facturacionAutonomo.product_service.infraestructure.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo de PUT /api/order/{publicId}/items/{itemPublicId}. Reemplaza lo que se puede cambiar de una línea:
 * la cantidad (obligatoria) y las notas (sin notas, null o "", las borra).
 */
public class UpdateLineaOrderRequest {

	@NotNull(message = "La cantidad de la línea es obligatoria")
	@Min(value = 1, message = "La cantidad tiene que ser al menos 1")
	private Integer quantity;

	@Size(max = 255, message = "Las notas no pueden superar los 255 caracteres")
	private String notes;

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

}
