package com.facturacionAutonomo.product_service.infraestructure.persistence.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import com.facturacionAutonomo.product_service.application.port.OUT.PaymentRepositoryPort;
import com.facturacionAutonomo.product_service.domain.exception.PaymentYaRegistradoException;
import com.facturacionAutonomo.product_service.domain.model.Payment;
import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.OrderEntity;
import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.PaymentEntity;
import com.facturacionAutonomo.product_service.infraestructure.persistence.mapper.PaymentEntityMapper;
import com.facturacionAutonomo.product_service.infraestructure.persistence.repository.SpringDataOrderEntityRepository;
import com.facturacionAutonomo.product_service.infraestructure.persistence.repository.SpringDataPaymentEntityRepository;

@Repository
public class JpaPaymentRepositoryAdapter implements PaymentRepositoryPort{

	private static final Logger log = LoggerFactory.getLogger(JpaPaymentRepositoryAdapter.class);

	private final SpringDataPaymentEntityRepository springpaymentrepository;
	private final SpringDataOrderEntityRepository springorderrepository;


	public JpaPaymentRepositoryAdapter(SpringDataPaymentEntityRepository springpaymentrepository,
			SpringDataOrderEntityRepository springorderrepository) {
		super();
		this.springpaymentrepository = springpaymentrepository;
		this.springorderrepository = springorderrepository;
	}


	@Override
	public Payment save(Payment pago) {
		log.debug("Guardando un cobro de {} del pedido {} del tenant {}", pago.getAmount(), pago.getOrderId(),
				pago.getTenantId());

		// 1. El pedido del cobro. getReferenceById no hace SELECT: solo hace falta el id para la clave foránea.
		//    Que el pedido exista y sea del tenant lo comprueba el servicio antes de llamar aquí.
		OrderEntity pedido = springorderrepository.getReferenceById(pago.getOrderId());

		// 2. Convertir el cobro a entidad.
		PaymentEntity entidad = PaymentEntityMapper.dominioToJpa(pago, pedido);

		// 3. Guardar. saveAndFlush (y no save) para que una violación del UNIQUE salte aquí dentro, en el catch.
		try {
			PaymentEntity guardado = springpaymentrepository.saveAndFlush(entidad);
			return PaymentEntityMapper.jpaToDominio(guardado);
		}catch(DataIntegrityViolationException ex) {
			// El servicio ya busca la clave antes de cobrar; esto cubre dos reintentos del TPV a la vez.
			String causa = String.valueOf(ex.getMostSpecificCause().getMessage());
			if(causa.contains("uk_payments_tenant_idempotency")) {
				throw new PaymentYaRegistradoException("Este cobro ya se ha registrado");
			}
			// Otra restricción (por ejemplo el CHECK de efectivo): no es un conflicto de negocio conocido.
			throw ex;
		}
	}


	@Override
	public Optional<Payment> findByTenantIdAndIdempotencyKey(UUID tenantId, String idempotencyKey) {
		if(idempotencyKey == null) {
			return Optional.empty();
		}
		Optional<PaymentEntity> encontrado = springpaymentrepository.findByTenantIdAndIdempotencyKey(tenantId, idempotencyKey);
		if(encontrado.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(PaymentEntityMapper.jpaToDominio(encontrado.get()));
	}


	@Override
	public List<Payment> findByTenantIdAndOrderId(UUID tenantId, Long orderId) {
		List<Payment> pagos = new ArrayList<>();
		for(PaymentEntity entidad : springpaymentrepository.findByTenantIdAndOrder_OrderIdOrderByCreatedAtAsc(tenantId, orderId)) {
			pagos.add(PaymentEntityMapper.jpaToDominio(entidad));
		}
		return pagos;
	}

}
