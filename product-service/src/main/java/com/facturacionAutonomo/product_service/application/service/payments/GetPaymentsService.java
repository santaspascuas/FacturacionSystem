package com.facturacionAutonomo.product_service.application.service.payments;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.product_service.application.port.OUT.PaymentRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.in.payment.GetPaymentsUseCase;
import com.facturacionAutonomo.product_service.application.query.GetPaymentsQuery;
import com.facturacionAutonomo.product_service.application.result.PaymentResult;
import com.facturacionAutonomo.product_service.application.service.finder.OrderFinder;
import com.facturacionAutonomo.product_service.domain.model.Order;
import com.facturacionAutonomo.product_service.domain.model.Payment;

@Service
public class GetPaymentsService implements GetPaymentsUseCase{

	private static final Logger log = LoggerFactory.getLogger(GetPaymentsService.class);

	private final PaymentRepositoryPort paymentrepository;
	private final OrderFinder finder;


	public GetPaymentsService(PaymentRepositoryPort paymentrepository, OrderFinder finder) {
		super();
		this.paymentrepository = paymentrepository;
		this.finder = finder;
	}


	@Override
	@Transactional(readOnly = true)
	public List<PaymentResult> execute(GetPaymentsQuery query) {

		finder.tenantObligatorio(query.tenantId());
		log.info("[GetPaymentsService] cobros de un pedido tenantId: {}", query.tenantId());

		// Se busca primero el pedido por tenant: un pedido de otro restaurante da 404 en vez de una lista vacía.
		Order order = finder.pedidoDelTenant(query.tenantId(), query.orderPublicId());

		List<PaymentResult> cobros = new ArrayList<>();
		for(Payment pago : paymentrepository.findByTenantIdAndOrderId(query.tenantId(), order.getId())) {
			cobros.add(PaymentResult.convertirA(pago));
		}
		return cobros;
	}

}
