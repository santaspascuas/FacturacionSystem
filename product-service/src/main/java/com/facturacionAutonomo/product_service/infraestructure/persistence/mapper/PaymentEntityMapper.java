package com.facturacionAutonomo.product_service.infraestructure.persistence.mapper;

import com.facturacionAutonomo.product_service.domain.model.Payment;
import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.OrderEntity;
import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.PaymentEntity;

// Puente entre la entidad JPA (con setters, la rellena Hibernate) y el dominio (sin setters, se crea con restoreData).
public class PaymentEntityMapper {

	// Solo tiene métodos estáticos: no se instancia (igual que el resto de mappers).
	private PaymentEntityMapper() {}

	/**
	 * Para guardar un cobro nuevo. El id no se copia: lo pone la secuencia al guardar.
	 * @param pedido el pedido del cobro. El dominio solo guarda su id (orderId); el adaptador le pasa la referencia
	 *               (getReferenceById, sin SELECT), igual que la mesa o los productos en OrderEntityMapper.
	 */
	public static PaymentEntity dominioToJpa(Payment pago, OrderEntity pedido) {
		PaymentEntity entidad = new PaymentEntity();
		entidad.setPublicId(pago.getPublicId());
		entidad.setTenantId(pago.getTenantId());
		entidad.setOrder(pedido);
		entidad.setAmount(pago.getAmount());
		entidad.setPaymentType(pago.getPaymentType());
		entidad.setCashReceived(pago.getCashReceived());
		entidad.setChangeGiven(pago.getChangeGiven());
		entidad.setIdempotencyKey(pago.getIdempotencyKey());
		entidad.setCreatedAt(pago.getCreatedAt());
		return entidad;
	}

	// El pedido es LAZY: getOrderId() de un proxy devuelve el id que ya tiene, sin consultar la tabla orders.
	public static Payment jpaToDominio(PaymentEntity entidad) {
		return Payment.restoreData(entidad.getPaymentId(), entidad.getTenantId(), entidad.getPublicId(),
				entidad.getOrder().getOrderId(), entidad.getAmount(), entidad.getPaymentType(),
				entidad.getCashReceived(), entidad.getChangeGiven(), entidad.getIdempotencyKey(),
				entidad.getCreatedAt());
	}

}
