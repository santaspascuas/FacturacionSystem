package com.facturacionAutonomo.product_service.application.result;

// Lo que devuelve registrar un cobro: el cobro (con el cambio que hay que devolver, si es en efectivo)
// y el pedido tal como queda después (paidAmount, pendingAmount y status: CIERRAYPAGA cuando está todo cobrado).
// Así el TPV repinta la cuenta sin tener que pedir el pedido otra vez.
public record RegistrarPaymentResult(
		PaymentResult payment,
		OrderResult order
		) {

}
