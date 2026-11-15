package com.facturacionAutonomo.product_service.application.service.finder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.facturacionAutonomo.product_service.application.command.orders.LineaOrderCommand;
import com.facturacionAutonomo.product_service.application.port.OUT.MenuItemRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.OUT.OrderRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.OUT.TablesRepositoryPort;
import com.facturacionAutonomo.product_service.application.result.OrderResult;
import com.facturacionAutonomo.product_service.domain.exception.MenuItemNotFoundException;
import com.facturacionAutonomo.product_service.domain.exception.OrderException;
import com.facturacionAutonomo.product_service.domain.exception.OrderNotFoundException;
import com.facturacionAutonomo.product_service.domain.model.MenuItem;
import com.facturacionAutonomo.product_service.domain.model.Order;
import com.facturacionAutonomo.product_service.domain.model.OrderItem;
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
	private final MenuItemRepositoryPort menuitemrepository;
	private static final Logger log = LoggerFactory.getLogger(OrderFinder.class);

	public OrderFinder(OrderRepositoryPort orderrepository, TablesRepositoryPort tablerepository,
			MenuItemRepositoryPort menuitemrepository) {
		super();
		this.orderrepository = orderrepository;
		this.tablerepository = tablerepository;
		this.menuitemrepository = menuitemrepository;
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

	/**
	 * Convierte las líneas que envía el TPV en líneas del pedido. Lo usan el alta y el añadir platos.
	 * - Todos los productos se buscan en UNA consulta (IN), no uno por línea: sin N+1.
	 * - Solo valen productos activos del tenant; si falta alguno, 404 y no se añade ninguna línea.
	 * - Nombre y precio se copian del producto ahora (foto del momento): el TPV solo dice qué y cuántos.
	 * - Si el mismo producto viene en dos líneas, son dos líneas (pueden llevar notas distintas para cocina).
	 * - Todas las líneas guardan la clave del envío (idempotencyKey, opcional) para detectar reintentos del TPV.
	 */
	public List<OrderItem> crearLineas(UUID tenantId, List<LineaOrderCommand> lineas, String idempotencyKey,
			LocalDateTime now) {
		if(lineas == null || lineas.isEmpty()) {
			throw new OrderException("El pedido necesita al menos una línea");
		}
		Set<UUID> idsPedidos = lineas.stream().map(LineaOrderCommand::menuItemPublicId).collect(Collectors.toSet());
		Map<UUID, MenuItem> productos = menuitemrepository.findActiveByTenantIdAndPublicIds(tenantId, idsPedidos)
				.stream().collect(Collectors.toMap(MenuItem::getPublicId, Function.identity()));
		if(productos.size() != idsPedidos.size()) {
			log.warn("[OrderFinder] productos pedidos: {}, encontrados activos: {}", idsPedidos, productos.keySet());
			throw new MenuItemNotFoundException("Algún producto del pedido no existe o no está disponible");
		}
		return lineas.stream().map(linea -> {
			MenuItem producto = productos.get(linea.menuItemPublicId());
			return OrderItem.crearOrderItem(producto.getId(), producto.getName(), producto.getBasePrice(),
					linea.quantity(), linea.notes(), idempotencyKey, now);
		}).toList();
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
