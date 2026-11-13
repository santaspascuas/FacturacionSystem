package com.facturacionAutonomo.product_service.infraestructure.web.dto;

import com.facturacionAutonomo.product_service.domain.model.TableZone;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Cuerpo de la modificación de los datos de una mesa (PUT). El tenant llega en la cabecera X-Tenant-Id y la mesa
 * en la ruta. El estado no va aquí: se cambia con PATCH /api/table/{publicId}/status (CambiarEstadoTablesRequest).
 * Todos los campos son obligatorios, como en el resto de PUT: si faltara uno se cambiaría sin querer.
 */
public class UpdateTablesRequest {

	@NotNull(message = "El número de mesa es obligatorio")
	@Min(value = 1, message = "El número de mesa tiene que ser al menos 1")
	private Integer tableNumber;

	@NotNull(message = "La capacidad de la mesa es obligatoria")
	@Min(value = 1, message = "La capacidad de la mesa tiene que ser al menos 1")
	private Integer capacity;

	// Un valor que no sea SALON, TERRAZA o BARRA no se puede leer y se responde 400.
	@NotNull(message = "La zona de la mesa es obligatoria")
	private TableZone zone;

	public Integer getTableNumber() {
		return tableNumber;
	}

	public void setTableNumber(Integer tableNumber) {
		this.tableNumber = tableNumber;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public TableZone getZone() {
		return zone;
	}

	public void setZone(TableZone zone) {
		this.zone = zone;
	}

}
