package com.facturacionAutonomo.product_service.infraestructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.PaymentEntity;

// Sin @EntityGraph: un pago no tiene colecciones, y su pedido (LAZY) no hace falta cargarlo para devolver el pago.
// Todas las consultas filtran por tenant: un pago de otro restaurante, para este tenant no existe.
public interface SpringDataPaymentEntityRepository extends JpaRepository<PaymentEntity, Long> {

	Optional<PaymentEntity> findByTenantIdAndPublicId(UUID tenantId, UUID publicId);

	// Reintentos del TPV: si ya hay un pago con esa clave, se devuelve en lugar de cobrar otra vez.
	// Usa el índice del UNIQUE uk_payments_tenant_idempotency.
	Optional<PaymentEntity> findByTenantIdAndIdempotencyKey(UUID tenantId, String idempotencyKey);

	// Los pagos de un pedido, en el orden en que se cobraron. "Order_OrderId" navega por la relación:
	// PaymentEntity.order -> OrderEntity.orderId, pero sin JOIN (la columna order_id ya está en payments).
	List<PaymentEntity> findByTenantIdAndOrder_OrderIdOrderByCreatedAtAsc(UUID tenantId, Long orderId);

}
