package com.facturacionAutonomo.documentacionfactura_service.application.result;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.facturacionAutonomo.documentacionfactura_service.domain.model.Order;
import com.facturacionAutonomo.documentacionfactura_service.domain.model.OrderStatus;
import com.facturacionAutonomo.documentacionfactura_service.domain.model.OrderType;

/**
 * Pedido de product-service tal como sale por la API de este servicio: con sus líneas, sus cobros y si está pagado
 * (si se podrá imprimir el ticket). El modelo de dominio Order nunca sale directamente por la API.
 */
public class OrderResult {
	private final UUID publicId;
	private final Integer orderNumber;
	private final OrderType orderType;
	private final OrderStatus status;
	private final Integer tableNumber;       // null en LLEVAR
	private final Integer numPersonas;
	private final BigDecimal baseImponible;
	private final BigDecimal tipoIva;
	private final BigDecimal cuotaIva;
	private final BigDecimal total;
	private final BigDecimal pendiente;
	private final LocalDateTime pagadoEn;    // UTC; null mientras quede algo pendiente
	private final boolean pagado;
	private final List<Linea> lineas;
	private final List<Cobro> cobros;

	private OrderResult(UUID publicId, Integer orderNumber, OrderType orderType, OrderStatus status,
			Integer tableNumber, Integer numPersonas, BigDecimal baseImponible, BigDecimal tipoIva,
			BigDecimal cuotaIva, BigDecimal total, BigDecimal pendiente, LocalDateTime pagadoEn, boolean pagado,
			List<Linea> lineas, List<Cobro> cobros) {
		this.publicId = publicId;
		this.orderNumber = orderNumber;
		this.orderType = orderType;
		this.status = status;
		this.tableNumber = tableNumber;
		this.numPersonas = numPersonas;
		this.baseImponible = baseImponible;
		this.tipoIva = tipoIva;
		this.cuotaIva = cuotaIva;
		this.total = total;
		this.pendiente = pendiente;
		this.pagadoEn = pagadoEn;
		this.pagado = pagado;
		this.lineas = lineas;
		this.cobros = cobros;
	}

	/** pagado sale de Order.estaPagado(): la regla vive en el dominio, el result solo la copia. */
	public static OrderResult from(Order pedido) {
		return new OrderResult(pedido.getPublicId(), pedido.getOrderNumber(), pedido.getOrderType(),
				pedido.getStatus(), pedido.getTableNumber(), pedido.getNumPersonas(), pedido.getBaseImponible(),
				pedido.getTipoIva(), pedido.getCuotaIva(), pedido.getTotal(), pedido.getPendiente(),
				pedido.getPagadoEn(), pedido.estaPagado(),
				pedido.getLineas().stream().map(Linea::from).toList(),
				pedido.getCobros().stream().map(Cobro::from).toList());
	}

	public UUID getPublicId() {
		return publicId;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public OrderType getOrderType() {
		return orderType;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public Integer getTableNumber() {
		return tableNumber;
	}

	public Integer getNumPersonas() {
		return numPersonas;
	}

	public BigDecimal getBaseImponible() {
		return baseImponible;
	}

	public BigDecimal getTipoIva() {
		return tipoIva;
	}

	public BigDecimal getCuotaIva() {
		return cuotaIva;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public BigDecimal getPendiente() {
		return pendiente;
	}

	public LocalDateTime getPagadoEn() {
		return pagadoEn;
	}

	/** Jackson lo saca como "pagado" (los getters de boolean empiezan por is). */
	public boolean isPagado() {
		return pagado;
	}

	public List<Linea> getLineas() {
		return lineas;
	}

	public List<Cobro> getCobros() {
		return cobros;
	}

	@Override
	public String toString() {
		return "OrderResult [publicId=" + publicId + ", orderNumber=" + orderNumber + ", status=" + status
				+ ", total=" + total + ", pagado=" + pagado + ", lineas=" + lineas.size() + ", cobros=" + cobros.size()
				+ "]";
	}
}
