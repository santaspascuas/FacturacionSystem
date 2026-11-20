package com.facturacionAutonomo.documentacionfactura_service.application.result;

import java.math.BigDecimal;

import com.facturacionAutonomo.documentacionfactura_service.domain.model.PaymentType;
import com.facturacionAutonomo.documentacionfactura_service.domain.model.TicketPayment;

/**
 * Un cobro del pedido tal como sale por la API de este servicio (dentro de OrderResult).
 * entregado y cambio solo tienen valor en efectivo; en tarjeta salen a null.
 */
public class Cobro {
	private final PaymentType formaPago;
	private final BigDecimal importe;
	private final BigDecimal entregado;
	private final BigDecimal cambio;

	private Cobro(PaymentType formaPago, BigDecimal importe, BigDecimal entregado, BigDecimal cambio) {
		this.formaPago = formaPago;
		this.importe = importe;
		this.entregado = entregado;
		this.cambio = cambio;
	}

	public static Cobro from(TicketPayment cobro) {
		return new Cobro(cobro.getFormaPago(), cobro.getImporte(), cobro.getEntregado(), cobro.getCambio());
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
		return "Cobro [formaPago=" + formaPago + ", importe=" + importe + ", entregado=" + entregado + ", cambio="
				+ cambio + "]";
	}
}
