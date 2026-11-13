package com.facturacionAutonomo.product_service.application.service.orders;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.product_service.application.port.OUT.OrderRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.in.order.GetOrdersUseCase;
import com.facturacionAutonomo.product_service.application.query.GetOrdersQuery;
import com.facturacionAutonomo.product_service.application.result.OrderResult;
import com.facturacionAutonomo.product_service.application.service.finder.OrderFinder;
import com.facturacionAutonomo.product_service.domain.model.Order;

@Service
public class GetOrdersService implements GetOrdersUseCase{

	private static final Logger log = LoggerFactory.getLogger(GetOrdersService.class);

	private final OrderRepositoryPort orderrepository;
	private final OrderFinder finder;


	public GetOrdersService(OrderRepositoryPort orderrepository, OrderFinder finder) {
		super();
		this.orderrepository = orderrepository;
		this.finder = finder;
	}


	// Pedidos con sus líneas (@EntityGraph) y sus mesas (OrderFinder.aResults): 2 consultas en total.
	@Override
	@Transactional(readOnly = true)
	public List<OrderResult> execute(GetOrdersQuery query) {

		finder.tenantObligatorio(query.tenantId());
		log.info("[GetOrdersService] listado de pedidos tenantId: {} soloActivas: {}", query.tenantId(), query.soloActivas());

		List<Order> pedidos = query.soloActivas()
				? orderrepository.findByTenantIdAndStatusIn(query.tenantId(), Order.ESTADOS_ABIERTOS)
				: orderrepository.findByTenantId(query.tenantId());
		return finder.aResults(pedidos);
	}

}
