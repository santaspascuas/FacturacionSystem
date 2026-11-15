package com.facturacionAutonomo.product_service.application.service.orders;

import java.time.Clock;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.product_service.application.command.orders.UpdateLineaOrderCommand;
import com.facturacionAutonomo.product_service.application.port.OUT.OrderRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.in.order.UpdateLineaOrderUseCase;
import com.facturacionAutonomo.product_service.application.result.OrderResult;
import com.facturacionAutonomo.product_service.application.service.finder.OrderFinder;
import com.facturacionAutonomo.product_service.domain.model.Order;

/**
 * Cambia la cantidad y las notas de una línea de un pedido abierto. El producto y su precio no cambian:
 * para otro producto se quita la línea y se añade una nueva.
 */
@Service
public class UpdateLineaOrderService implements UpdateLineaOrderUseCase {

	private static final Logger log = LoggerFactory.getLogger(UpdateLineaOrderService.class);

	private final OrderRepositoryPort orderrepository;
	private final OrderFinder finder;
	private final Clock clock;


	public UpdateLineaOrderService(OrderRepositoryPort orderrepository, OrderFinder finder, Clock clock) {
		super();
		this.orderrepository = orderrepository;
		this.finder = finder;
		this.clock = clock;
	}


	@Override
	@Transactional
	public OrderResult execute(UpdateLineaOrderCommand command) {
		finder.tenantObligatorio(command.tenantId());
		log.info("[UpdateLineaOrderService] cambiar línea {} del pedido {} tenantId: {}", command.itemPublicId(),
				command.orderPublicId(), command.tenantId());

		Order order = finder.pedidoDelTenant(command.tenantId(), command.orderPublicId());

		// El dominio decide: 400 si el pedido está cerrado, si la línea no es de este pedido o si la cantidad no vale.
		// Si la cantidad sube, el pedido vuelve a ENVIADO (hay más que cocinar).
		LocalDateTime ahora = LocalDateTime.now(clock);
		order.cambiarCantidad(command.itemPublicId(), command.quantity(), ahora);
		order.cambiarNotas(command.itemPublicId(), command.notes(), ahora);

		return finder.aResult(orderrepository.update(order));
	}

}
