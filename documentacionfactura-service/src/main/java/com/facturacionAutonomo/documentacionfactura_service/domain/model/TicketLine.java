package com.facturacionAutonomo.documentacionfactura_service.domain.model;

import java.math.BigDecimal;

/**
 * Una línea vendida: lo que llega del pedido y lo que se copia en el ticket (es el mismo dato, por eso una sola
 * clase para los dos). Precios con IVA incluido, como en la carta. Inmutable.
 */
public class TicketLine {
	private final String descripcion;
	private final int cantidad;
	private final BigDecimal precioUnitario;
	private final BigDecimal importe;

	private TicketLine(String descripcion, int cantidad, BigDecimal precioUnitario, BigDecimal importe) {
		this.descripcion = descripcion;
		this.cantidad = cantidad;
		this.precioUnitario = precioUnitario;
		this.importe = importe;
	}

	/** Viene de product-service o de la BD. Que los importes cuadren lo comprueba Ticket.validarEmision. */
	public static TicketLine restoreData(String descripcion, int cantidad, BigDecimal precioUnitario,
			BigDecimal importe) {
		return new TicketLine(descripcion, cantidad, precioUnitario, importe);
	}

	public String getDescripcion() {
		return descripcion;
	}

	public int getCantidad() {
		return cantidad;
	}

	public BigDecimal getPrecioUnitario() {
		return precioUnitario;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	@Override
	public String toString() {
		return "TicketLine [descripcion=" + descripcion + ", cantidad=" + cantidad + ", precioUnitario="
				+ precioUnitario + ", importe=" + importe + "]";
	}
}
