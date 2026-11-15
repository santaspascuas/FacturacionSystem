package com.facturacionAutonomo.product_service.application.command.payments;

import java.math.BigDecimal;
import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.PaymentType;

/**
 * Registrar un cobro de un pedido. El TPV solo manda la cantidad: lo que se aplica al pedido, el efectivo y el
 * cambio los calcula el dominio (Payment.crearUnPayment).
 *   - EFECTIVO: cantidad = lo que entrega el cliente. Se aplica min(cantidad, pendiente) y el resto es cambio.
 *   - TARJETA: cantidad = lo que se pasa por el datáfono. Se aplica entera; si supera lo pendiente, se rechaza.
 * Regla para el frontal (decidida): en un pago mixto, registrar primero la parte con tarjeta y después el efectivo.
 * Si el efectivo va antes y entrega más de lo pendiente, se queda con todo y la tarjeta ya no cabe.
 */
public record RegistrarPaymentCommand(
		UUID tenantId,
		UUID orderPublicId,
		PaymentType paymentType,
		BigDecimal cantidad,
		String idempotencyKey   // opcional; si el TPV reintenta con la misma clave, no se cobra dos veces
		) {

}
