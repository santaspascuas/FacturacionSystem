package com.facturacionAutonomo.product_service.infraestructure.web.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.OrderType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo del alta de un pedido con sus líneas. El tenant llega en la cabecera X-Tenant-Id.
 * Que COMERAQUI lleve mesa y LLEVAR no lo valida el dominio (depende de dos campos a la vez).
 */
public class RegistrarOrderRequest {

	// Un valor que no sea COMERAQUI o LLEVAR no se puede leer y se responde 400.
	@NotNull(message = "El tipo de pedido es obligatorio")
	private OrderType orderType;

	private UUID tablePublicId;

	@Min(value = 1, message = "El número de personas tiene que ser al menos 1")
	private Integer numPersonas;

	@Size(max = 100, message = "La clave de idempotencia no puede superar los 100 caracteres")
	private String idempotencyKey;

	private LocalDateTime clientTimestamp;

	// @NotEmpty: al menos una línea. @Valid: valida también CADA línea por dentro (@NotNull, @Min...);
	// sin @Valid solo se validaría la lista, no su contenido.
	@NotEmpty(message = "El pedido necesita al menos una línea")
	private List<@Valid LineaOrderRequest> lineas;

	public OrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

	public UUID getTablePublicId() {
		return tablePublicId;
	}

	public void setTablePublicId(UUID tablePublicId) {
		this.tablePublicId = tablePublicId;
	}

	public Integer getNumPersonas() {
		return numPersonas;
	}

	public void setNumPersonas(Integer numPersonas) {
		this.numPersonas = numPersonas;
	}

	public String getIdempotencyKey() {
		return idempotencyKey;
	}

	public void setIdempotencyKey(String idempotencyKey) {
		this.idempotencyKey = idempotencyKey;
	}

	public LocalDateTime getClientTimestamp() {
		return clientTimestamp;
	}

	public void setClientTimestamp(LocalDateTime clientTimestamp) {
		this.clientTimestamp = clientTimestamp;
	}

	public List<LineaOrderRequest> getLineas() {
		return lineas;
	}

	public void setLineas(List<LineaOrderRequest> lineas) {
		this.lineas = lineas;
	}

}
