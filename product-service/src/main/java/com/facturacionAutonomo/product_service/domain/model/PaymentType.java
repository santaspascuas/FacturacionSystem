package com.facturacionAutonomo.product_service.domain.model;

// Forma de pago de un cobro. Tiene que coincidir con el CHECK de payments.payment_type (V11):
// si se añade un valor aquí (p. ej. BIZUM), hace falta una migración nueva que amplíe el CHECK.
public enum PaymentType {
	EFECTIVO,
	TARJETA
}
