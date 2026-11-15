package com.facturacionAutonomo.product_service.application.service.orders;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.product_service.application.command.orders.AnadirLineasOrderCommand;
import com.facturacionAutonomo.product_service.application.port.OUT.OrderRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.in.order.AnadirLineasOrderUseCase;
import com.facturacionAutonomo.product_service.application.result.OrderResult;
import com.facturacionAutonomo.product_service.application.service.finder.OrderFinder;
import com.facturacionAutonomo.product_service.domain.model.Order;
import com.facturacionAutonomo.product_service.domain.model.OrderItem;

/**
 * Añade platos a un pedido abierto (el cliente pide otra ronda o el postre).
 * Las líneas nuevas copian nombre y precio del producto en este momento; el IVA es el que ya tiene el pedido
 * (se fijó al abrirlo), así que el pedido entero se calcula con un único tipo.
 */
@Service
public class AnadirLineasOrderService implements AnadirLineasOrderUseCase {

	private static final Logger log = LoggerFactory.getLogger(AnadirLineasOrderService.class);

	private final OrderRepositoryPort orderrepository;
	private final OrderFinder finder;
	private final Clock clock;


	public AnadirLineasOrderService(OrderRepositoryPort orderrepository, OrderFinder finder, Clock clock) {
		super();
		this.orderrepository = orderrepository;
		this.finder = finder;
		this.clock = clock;
	}


	@Override
	@Transactional
	public OrderResult execute(AnadirLineasOrderCommand command) {
		finder.tenantObligatorio(command.tenantId());
		log.info("[AnadirLineasOrderService] añadir {} líneas al pedido {} tenantId: {}",
				command.lineas() != null ? command.lineas().size() : 0, command.orderPublicId(), command.tenantId());

		Order order = finder.pedidoDelTenant(command.tenantId(), command.orderPublicId());

		// Reintento del TPV: ese envío ya está en el pedido. Se devuelve el pedido tal cual, sin duplicar líneas.
		// Va antes que cualquier otra comprobación: si el pedido se cobró después, el reintento sigue siendo un 200.
		if(order.envioYaAplicado(command.idempotencyKey())) {
			log.info("[AnadirLineasOrderService] envío {} ya aplicado al pedido {}", command.idempotencyKey(),
					command.orderPublicId());
			return finder.aResult(order);
		}

		LocalDateTime ahora = LocalDateTime.now(clock);
		List<OrderItem> lineas = finder.crearLineas(command.tenantId(), command.lineas(), command.idempotencyKey(), ahora);
		lineas.forEach(linea -> order.addItem(linea, ahora));

		return finder.aResult(orderrepository.update(order));
	}

}
