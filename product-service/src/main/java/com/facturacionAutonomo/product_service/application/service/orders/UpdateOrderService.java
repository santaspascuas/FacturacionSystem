package com.facturacionAutonomo.product_service.application.service.orders;

import java.time.Clock;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.product_service.application.command.orders.UpdateOrderCommand;
import com.facturacionAutonomo.product_service.application.port.OUT.OrderRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.in.order.UpdateOrderUseCase;
import com.facturacionAutonomo.product_service.application.result.OrderResult;
import com.facturacionAutonomo.product_service.application.service.finder.OrderFinder;
import com.facturacionAutonomo.product_service.domain.model.Order;

/**
 * Cambia los datos del pedido que se pueden tocar después de abrirlo: solo el número de personas.
 * El tipo y la mesa quedan fijos; las líneas y el estado tienen sus propios casos de uso.
 */
@Service
public class UpdateOrderService implements UpdateOrderUseCase {

	private static final Logger log = LoggerFactory.getLogger(UpdateOrderService.class);

	private final OrderRepositoryPort orderrepository;
	private final OrderFinder finder;
	private final Clock clock;


	public UpdateOrderService(OrderRepositoryPort orderrepository, OrderFinder finder, Clock clock) {
		super();
		this.orderrepository = orderrepository;
		this.finder = finder;
		this.clock = clock;
	}


	@Override
	@Transactional
	public OrderResult execute(UpdateOrderCommand command) {
		finder.tenantObligatorio(command.tenantId());
		log.info("[UpdateOrderService] cambiar personas del pedido {} tenantId: {}", command.publicId(),
				command.tenantId());

		Order order = finder.pedidoDelTenant(command.tenantId(), command.publicId());

		// 400 si el pedido está cerrado o si el número de personas es menor que 1 (null vale: para llevar).
		order.cambiarPersonas(command.numPersonas(), LocalDateTime.now(clock));

		return finder.aResult(orderrepository.update(order));
	}

}
