package com.facturacionAutonomo.product_service.infraestructure.web.dto;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Una línea del pedido tal como la envía el TPV. Sin nombre ni precio: los pone el servidor a partir del producto.
 * Los límites coinciden con los de order_items.
 */
public class LineaOrderRequest {

	@NotNull(message = "El producto de la línea es obligatorio")
	private UUID menuItemPublicId;

	@NotNull(message = "La cantidad de la línea es obligatoria")
	@Min(value = 1, message = "La cantidad tiene que ser al menos 1")
	private Integer quantity;

	@Size(max = 255, message = "Las notas no pueden superar los 255 caracteres")
	private String notes;

	public UUID getMenuItemPublicId() {
		return menuItemPublicId;
	}

	public void setMenuItemPublicId(UUID menuItemPublicId) {
		this.menuItemPublicId = menuItemPublicId;
	}

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
