package com.facturacionAutonomo.product_service.infraestructure.web.dto;

import com.facturacionAutonomo.product_service.domain.model.TableStatus;

import jakarta.validation.constraints.NotNull;

/**
 * Cuerpo del cambio de estado de una mesa (PATCH /api/table/{publicId}/status).
 * Un valor que no sea DISPONIBLE, OCUPADA, RESERVADA o LIMPIANDO no se puede leer y se responde 400.
 */
public class CambiarEstadoTablesRequest {

	@NotNull(message = "El estado de la mesa es obligatorio")
	private TableStatus status;

	public TableStatus getStatus() {
		return status;
	}

	public void setStatus(TableStatus status) {
		this.status = status;
	}

}
