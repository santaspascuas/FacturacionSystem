package com.facturacionAutonomo.product_service.domain.exception;

// Ya hay un cobro con esa clave de idempotencia en el tenant (responde 409).
// Pasa si el TPV reintenta el mismo cobro dos veces a la vez: el segundo choca con el UNIQUE de payments.
public class PaymentYaRegistradoException extends RuntimeException{
	public PaymentYaRegistradoException(String message) {
		super(message);
	}

}
