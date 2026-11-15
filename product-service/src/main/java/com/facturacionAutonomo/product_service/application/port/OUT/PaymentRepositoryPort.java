package com.facturacionAutonomo.product_service.application.port.OUT;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.Payment;

// Puerto de salida de los cobros. Solo habla de dominio (Payment), nunca de PaymentEntity:
// la aplicación no sabe que detrás hay JPA.
// Sin update ni delete: un cobro es un registro de caja inmutable. Si hay un error, se registra otro cobro.
public interface PaymentRepositoryPort {

	Payment save(Payment pago);

	// Reintentos del TPV: si ya hay un cobro con esa clave, el servicio lo devuelve en lugar de cobrar otra vez.
	// Con clave null devuelve vacío (un cobro sin clave no se puede reintentar).
	Optional<Payment> findByTenantIdAndIdempotencyKey(UUID tenantId, String idempotencyKey);

	// Los cobros de un pedido, en el orden en que se hicieron.
	List<Payment> findByTenantIdAndOrderId(UUID tenantId, Long orderId);

}
