package com.facturacionAutonomo.product_service.application.service.orders;

import java.time.Clock;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.product_service.application.command.orders.CambiarEstadoOrderCommand;
import com.facturacionAutonomo.product_service.application.port.OUT.OrderRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.in.order.CambiarEstadoOrderUseCase;
import com.facturacionAutonomo.product_service.application.result.OrderResult;
import com.facturacionAutonomo.product_service.application.service.finder.OrderFinder;
import com.facturacionAutonomo.product_service.domain.model.Order;

@Service
public class CambiarEstadoOrderService implements CambiarEstadoOrderUseCase{

	private static final Logger log = LoggerFactory.getLogger(CambiarEstadoOrderService.class);

	private final OrderRepositoryPort orderrepository;
	private final OrderFinder finder;
	private final Clock clock;


	public CambiarEstadoOrderService(OrderRepositoryPort orderrepository, OrderFinder finder, Clock clock) {
		super();
		this.orderrepository = orderrepository;
		this.finder = finder;
		this.clock = clock;
	}


	@Override
	@Transactional
	public OrderResult execute(CambiarEstadoOrderCommand command) {

		finder.tenantObligatorio(command.tenantId());
		log.info("[CambiarEstadoOrderService] cambio de estado de pedido tenantId: {}", command.tenantId());

		Order order = finder.pedidoDelTenant(command.tenantId(), command.orderPublicId());

		// El dominio decide: 400 si el pedido ya está cerrado (pagado o cancelado) o si el estado es null.
		// Al pasar a LISTO rellena cookedAt.
		order.cambiarEstado(command.status(), LocalDateTime.now(clock));

		return finder.aResult(orderrepository.update(order));
	}

}
