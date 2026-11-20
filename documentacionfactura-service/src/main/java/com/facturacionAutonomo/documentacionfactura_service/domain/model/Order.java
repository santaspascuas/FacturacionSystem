package com.facturacionAutonomo.documentacionfactura_service.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Pedido de product-service, con sus líneas y sus cobros, tal como lo necesita este servicio para emitir el ticket.
 * Es lo que devuelve OrderPort: el resto de la aplicación no ve el JSON remoto. Inmutable.
 * Importes de product-service: base + cuota = total, y las líneas llevan el IVA incluido.
 */
public class Order {
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
	private final LocalDateTime pagadoEn;    // UTC
	private final List<TicketLine> lineas;
	private final List<TicketPayment> cobros;

	private Order(UUID publicId, Integer orderNumber, OrderType orderType, OrderStatus status, Integer tableNumber,
			Integer numPersonas, BigDecimal baseImponible, BigDecimal tipoIva, BigDecimal cuotaIva, BigDecimal total,
			BigDecimal pendiente, LocalDateTime pagadoEn, List<TicketLine> lineas, List<TicketPayment> cobros) {
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
		// Copia propia: si quien llama modifica su lista después, el pedido no cambia. Los getters la protegen al salir.
		this.lineas = (lineas == null) ? new ArrayList<>() : new ArrayList<>(lineas);
		this.cobros = (cobros == null) ? new ArrayList<>() : new ArrayList<>(cobros);
	}

	/**
	 * El pedido ya existe en product-service: aquí solo se reconstruye. No valida; si se puede imprimir lo decide
	 * Ticket.validarEmision.
	 */
	public static Order restoreData(UUID publicId, Integer orderNumber, OrderType orderType, OrderStatus status,
			Integer tableNumber, Integer numPersonas, BigDecimal baseImponible, BigDecimal tipoIva,
			BigDecimal cuotaIva, BigDecimal total, BigDecimal pendiente, LocalDateTime pagadoEn,
			List<TicketLine> lineas, List<TicketPayment> cobros) {
		return new Order(publicId, orderNumber, orderType, status, tableNumber, numPersonas, baseImponible, tipoIva,
				cuotaIva, total, pendiente, pagadoEn, lineas, cobros);
	}

	/** Pagado del todo: cerrado por el cobro y sin nada pendiente. */
	public boolean estaPagado() {
		return status == OrderStatus.CIERRAYPAGA && pendiente != null && pendiente.signum() == 0;
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

	/**
	 * Vista de solo lectura: add, remove o clear lanzan UnsupportedOperationException. Así nadie puede cambiar las
	 * líneas del pedido desde fuera. No copia: envuelve la lista interna, a la que nadie más tiene acceso.
	 */
	public List<TicketLine> getLineas() {
		return Collections.unmodifiableList(lineas);
	}

	/** Vista de solo lectura, como getLineas(). */
	public List<TicketPayment> getCobros() {
		return Collections.unmodifiableList(cobros);
	}

	@Override
	public String toString() {
		return "Order [publicId=" + publicId + ", orderNumber=" + orderNumber + ", status=" + status + ", total="
				+ total + ", pendiente=" + pendiente + ", lineas=" + lineas.size() + ", cobros=" + cobros.size() + "]";
	}
}
