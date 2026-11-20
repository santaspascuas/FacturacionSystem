package com.facturacionAutonomo.documentacionfactura_service.application.result;

import java.math.BigDecimal;

import com.facturacionAutonomo.documentacionfactura_service.domain.model.TicketLine;

/** Una línea del pedido tal como sale por la API de este servicio (dentro de OrderResult). Precios con IVA. */
public class Linea {
	private final String descripcion;
	private final Integer cantidad;
	private final BigDecimal precioUnitario;
	private final BigDecimal importe;

	private Linea(String descripcion, Integer cantidad, BigDecimal precioUnitario, BigDecimal importe) {
		this.descripcion = descripcion;
		this.cantidad = cantidad;
		this.precioUnitario = precioUnitario;
		this.importe = importe;
	}

	public static Linea from(TicketLine linea) {
		return new Linea(linea.getDescripcion(), linea.getCantidad(), linea.getPrecioUnitario(), linea.getImporte());
	}

	public String getDescripcion() {
		return descripcion;
	}

	public Integer getCantidad() {
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
		return "Linea [descripcion=" + descripcion + ", cantidad=" + cantidad + ", precioUnitario=" + precioUnitario
				+ ", importe=" + importe + "]";
	}
}
