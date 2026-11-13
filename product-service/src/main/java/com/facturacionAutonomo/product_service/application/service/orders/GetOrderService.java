package com.facturacionAutonomo.product_service.application.service.orders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.product_service.application.port.in.order.GetOrderUseCase;
import com.facturacionAutonomo.product_service.application.query.GetOrderQuery;
import com.facturacionAutonomo.product_service.application.result.OrderResult;
import com.facturacionAutonomo.product_service.application.service.finder.OrderFinder;

@Service
public class GetOrderService implements GetOrderUseCase{

	private static final Logger log = LoggerFactory.getLogger(GetOrderService.class);

	private final OrderFinder finder;


	public GetOrderService(OrderFinder finder) {
		super();
		this.finder = finder;
	}


	@Override
	@Transactional(readOnly = true)
	public OrderResult execute(GetOrderQuery query) {

		finder.tenantObligatorio(query.tenantId());
		log.info("[GetOrderService] consulta de pedido tenantId: {}", query.tenantId());

		return finder.aResult(finder.pedidoDelTenant(query.tenantId(), query.publicId()));
	}

}
