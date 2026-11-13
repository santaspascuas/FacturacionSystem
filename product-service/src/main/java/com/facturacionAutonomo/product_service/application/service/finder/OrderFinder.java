package com.facturacionAutonomo.product_service.application.service.finder;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.facturacionAutonomo.product_service.application.port.OUT.OrderRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.OUT.TablesRepositoryPort;
import com.facturacionAutonomo.product_service.application.result.OrderResult;
import com.facturacionAutonomo.product_service.domain.exception.OrderException;
import com.facturacionAutonomo.product_service.domain.exception.OrderNotFoundException;
import com.facturacionAutonomo.product_service.domain.model.Order;
import com.facturacionAutonomo.product_service.domain.model.TableRestautant;

/**
 * Búsquedas que comparten los casos de uso de pedidos. Todas filtran por tenant:
 * un pedido de otro restaurante, para este tenant no existe.
 * También convierte pedidos a OrderResult: el dominio Order solo guarda el id interno de la mesa,
 * y la respuesta necesita su publicId y su número.
 */
@Component
public class OrderFinder {

	private final OrderRepositoryPort orderrepository;
	private final TablesRepositoryPort tablerepository;
	private static final Logger log = LoggerFactory.getLogger(OrderFinder.class);

	public OrderFinder(OrderRepositoryPort orderrepository, TablesRepositoryPort tablerepository) {
		super();
		this.orderrepository = orderrepository;
		this.tablerepository = tablerepository;
	}

	public void tenantObligatorio(UUID tenantId) {
		if(tenantId == null) {
			throw new OrderException("El tenant es obligatorio");
		}
	}

	public Order pedidoDelTenant(UUID tenantId, UUID publicId) {
		if(publicId == null) {
			throw new OrderException("El pedido es obligatorio");
		}
		return orderrepository.findByTenantIdAndPublicId(tenantId, publicId)
				.orElseThrow(() -> new OrderNotFoundException("El pedido no se ha encontrado"));
	}

	public OrderResult aResult(Order order) {
		return aResults(List.of(order)).get(0);
	}
	
	
	public List<OrderResult> aResults(List<Order> orders) {
		log.info("Ejecutamos el finder para obtener la lista de ordenes");
		for(Order order: orders) {
			System.out.println("obtenemos los publicID" +order.getPublicId());
		}
		
		
		
		
		
		
		List<Long> tableIds = orders.stream().map(Order::getTableId).filter(Objects::nonNull).distinct().toList();
		Map<Long, TableRestautant> mesas = tablerepository.findByIds(tableIds).stream()
				.collect(Collectors.toMap(TableRestautant::getId, Function.identity()));
		return orders.stream()
				.map(order -> OrderResult.convertirA(order, order.getTableId() != null ? mesas.get(order.getTableId()) : null))
				.toList();
	}

}
