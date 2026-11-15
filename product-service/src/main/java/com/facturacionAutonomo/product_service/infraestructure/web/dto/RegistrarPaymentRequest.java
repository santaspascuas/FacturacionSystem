package com.facturacionAutonomo.product_service.infraestructure.web.dto;

import java.math.BigDecimal;

import com.facturacionAutonomo.product_service.domain.model.PaymentType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo del registro de un cobro. El tenant llega en la cabecera X-Tenant-Id y el pedido en la ruta.
 *   - EFECTIVO: cantidad = lo que entrega el cliente (puede ser más de lo pendiente: el resto es cambio).
 *   - TARJETA: cantidad = lo que se pasa por el datáfono (nunca más de lo pendiente).
 * En un pago mixto, el frontal debe registrar primero la tarjeta y después el efectivo.
 * Los límites coinciden con DECIMAL(10,2) de payments.
 */
public class RegistrarPaymentRequest {

	// Un valor que no sea EFECTIVO o TARJETA no se puede leer y se responde 400.
	@NotNull(message = "La forma de pago es obligatoria")
	private PaymentType paymentType;

	@NotNull(message = "La cantidad es obligatoria")
	@DecimalMin(value = "0.01", message = "La cantidad tiene que ser mayor que 0")
	@Digits(integer = 8, fraction = 2, message = "La cantidad admite hasta 8 enteros y 2 decimales")
	private BigDecimal cantidad;

	@Size(max = 100, message = "La clave de idempotencia no puede superar los 100 caracteres")
	private String idempotencyKey;

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public BigDecimal getCantidad() {
		return cantidad;
	}

	public void setCantidad(BigDecimal cantidad) {
		this.cantidad = cantidad;
	}

	public String getIdempotencyKey() {
		return idempotencyKey;
	}

	public void setIdempotencyKey(String idempotencyKey) {
		this.idempotencyKey = idempotencyKey;
	}

}
