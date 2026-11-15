package com.facturacionAutonomo.product_service.domain.exception;

// Datos de un cobro que no son válidos (importe, forma de pago, efectivo entregado...). Responde 400.
public class PaymentException extends RuntimeException{
	public PaymentException(String message) {
		super(message);
	}

}
