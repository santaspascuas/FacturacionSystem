package com.facturacionAutonomo.product_service.application.service.orders;

import java.time.Clock;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.product_service.application.command.orders.EliminarLineaOrderCommand;
import com.facturacionAutonomo.product_service.application.port.OUT.OrderRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.in.order.EliminarLineaOrderUseCase;
import com.facturacionAutonomo.product_service.application.result.OrderResult;
import com.facturacionAutonomo.product_service.application.service.finder.OrderFinder;
import com.facturacionAutonomo.product_service.domain.model.Order;

/**
 * Quita un plato de un pedido abierto. El pedido en sí no se borra nunca (es historial de facturación):
 * para anularlo entero se cambia su estado a CANCELADO.
 */
@Service
public class EliminarLineaOrderService implements EliminarLineaOrderUseCase {

	private static final Logger log = LoggerFactory.getLogger(EliminarLineaOrderService.class);

	private final OrderRepositoryPort orderrepository;
	private final OrderFinder finder;
	private final Clock clock;


	public EliminarLineaOrderService(OrderRepositoryPort orderrepository, OrderFinder finder, Clock clock) {
		super();
		this.orderrepository = orderrepository;
		this.finder = finder;
		this.clock = clock;
	}


	@Override
	@Transactional
	public OrderResult execute(EliminarLineaOrderCommand command) {
		finder.tenantObligatorio(command.tenantId());
		log.info("[EliminarLineaOrderService] quitar línea {} del pedido {} tenantId: {}", command.itemPublicId(),
				command.orderPublicId(), command.tenantId());

		Order order = finder.pedidoDelTenant(command.tenantId(), command.orderPublicId());

		// El dominio decide: 400 si el pedido está cerrado, si la línea no es suya o si es la última.
		// Con orphanRemoval, el adaptador hace DELETE de la línea al guardar.
		order.quitarItem(command.itemPublicId(), LocalDateTime.now(clock));

		return finder.aResult(orderrepository.update(order));
	}

}
