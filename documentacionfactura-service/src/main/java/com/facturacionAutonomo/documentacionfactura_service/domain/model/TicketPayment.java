package com.facturacionAutonomo.documentacionfactura_service.domain.model;

import java.math.BigDecimal;

/**
 * Un cobro del pedido, copiado en el ticket para imprimir "Tarjeta / Efectivo entregado / Cambio". Inmutable.
 * importe es lo que el cobro aplica al pedido; entregado y cambio solo existen en efectivo (null en tarjeta).
 */
public class TicketPayment {
	private final PaymentType formaPago;
	private final BigDecimal importe;
	private final BigDecimal entregado;
	private final BigDecimal cambio;

	private TicketPayment(PaymentType formaPago, BigDecimal importe, BigDecimal entregado, BigDecimal cambio) {
		this.formaPago = formaPago;
		this.importe = importe;
		this.entregado = entregado;
		this.cambio = cambio;
	}

	/** Viene de product-service o de la BD. Que los importes cuadren lo comprueba Ticket.validarEmision. */
	public static TicketPayment restoreData(PaymentType formaPago, BigDecimal importe, BigDecimal entregado,
			BigDecimal cambio) {
		return new TicketPayment(formaPago, importe, entregado, cambio);
	}

	public PaymentType getFormaPago() {
		return formaPago;
	}

	public BigDecimal getImporte() {
		return importe;
	}

	public BigDecimal getEntregado() {
		return entregado;
	}

	public BigDecimal getCambio() {
		return cambio;
	}

	@Override
	public String toString() {
		return "TicketPayment [formaPago=" + formaPago + ", importe=" + importe + ", entregado=" + entregado
				+ ", cambio=" + cambio + "]";
	}
}
