package com.facturacionAutonomo.product_service.infraestructure.persistence.adapter;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import com.facturacionAutonomo.product_service.application.port.OUT.OrderRepositoryPort;
import com.facturacionAutonomo.product_service.domain.exception.MesaConPedidoAbiertoException;
import com.facturacionAutonomo.product_service.domain.exception.OrderNotFoundException;
import com.facturacionAutonomo.product_service.domain.exception.OrderYaRegistradaException;
import com.facturacionAutonomo.product_service.domain.model.Order;
import com.facturacionAutonomo.product_service.domain.model.OrderItem;
import com.facturacionAutonomo.product_service.domain.model.OrderStatus;
import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.MenuItemEntity;
import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.OrderEntity;
import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.TablesEntity;
import com.facturacionAutonomo.product_service.infraestructure.persistence.mapper.OrderEntityMapper;
import com.facturacionAutonomo.product_service.infraestructure.persistence.repository.SpringDataMenuItemRepository;
import com.facturacionAutonomo.product_service.infraestructure.persistence.repository.SpringDataOrderEntityRepository;
import com.facturacionAutonomo.product_service.infraestructure.persistence.repository.SpringDataTablesRepository;


@Repository
public class JpaOrderRepositoryAdapter implements OrderRepositoryPort{

	private static final Logger log = LoggerFactory.getLogger(JpaOrderRepositoryAdapter.class);

	private final SpringDataOrderEntityRepository springrepositoryorder;
	private final SpringDataMenuItemRepository springitemrepository;
	private final SpringDataTablesRepository springtablesrepository;


	public JpaOrderRepositoryAdapter(SpringDataOrderEntityRepository springrepositoryorder,
			SpringDataMenuItemRepository springitemrepository, SpringDataTablesRepository springtablesrepository) {
		super();
		this.springrepositoryorder = springrepositoryorder;
		this.springitemrepository = springitemrepository;
		this.springtablesrepository = springtablesrepository;
	}


	@Override
	public Order save(Order order) {
		log.debug("Guardando el pedido {} del tenant {}", order.getOrderNumber(), order.getTenantId());
		//el mapper convierte el pedido incluidos sus items; el adaptador le da la mesa y los productos.
		// Que la mesa y los productos existan y sean del tenant lo comprueba el servicio antes de llamar aquí.

		// 1. La mesa (los pedidos para llevar no tienen).
		TablesEntity mesa = null;
		if(order.getTableId() != null) {
			mesa = springtablesrepository.getReferenceById(order.getTableId());
		}

		// 2. Los productos de las líneas, preparados en un Map para que el mapper solo tenga que consultarlo.
		Map<Long, MenuItemEntity> productos = referenciasDeProductos(order);

		// 3. Convertir el pedido entero (con sus líneas) a entidad.
		OrderEntity entidad = OrderEntityMapper.dominioToJpa(order, mesa, productos);
		try {
			// cascade = ALL en OrderEntity.items: un solo saveAndFlush inserta el pedido Y sus líneas.
			// saveAndFlush (y no save) para que una violación de UNIQUE salte aquí dentro, en el catch.
			return OrderEntityMapper.jpaToDominio(springrepositoryorder.saveAndFlush(entidad));
		}catch(DataIntegrityViolationException ex) {
			throw traducirConflicto(ex, order);
		}
	}


	@Override
	public Order update(Order order) {
		log.debug("Actualizando el pedido {} del tenant {}", order.getOrderNumber(), order.getTenantId());
		// Se parte de la entidad gestionada (con sus líneas, por el @EntityGraph) para conservar su @Version:
		// una entidad nueva con version null la trataría Hibernate como nueva y haría un INSERT.
		OrderEntity entidad = springrepositoryorder.findByTenantIdAndPublicId(order.getTenantId(), order.getPublicId())
				.orElseThrow(() -> new OrderNotFoundException("El pedido no se ha encontrado"));
		Map<Long, MenuItemEntity> productos = referenciasDeProductos(order);
		OrderEntityMapper.actualizarJpa(entidad, order, productos);
		try {
			return OrderEntityMapper.jpaToDominio(springrepositoryorder.saveAndFlush(entidad));
		}catch(DataIntegrityViolationException ex) {
			throw traducirConflicto(ex, order);
		}
	}


	@Override
	public List<Order> findByTenantId(UUID tenantId) {
		return springrepositoryorder.findByTenantIdOrderByCreatedAtDesc(tenantId).stream()
				.map(OrderEntityMapper::jpaToDominio).toList();
	}


	@Override
	public Optional<Order> findByTenantIdAndPublicId(UUID tenantId, UUID publicId) {
		return springrepositoryorder.findByTenantIdAndPublicId(tenantId, publicId).map(OrderEntityMapper::jpaToDominio);
	}


	@Override
	public Optional<Order> findByTenantIdAndIdempotencyKey(UUID tenantId, String idempotencyKey) {
		return springrepositoryorder.findByTenantIdAndIdempotencyKey(tenantId, idempotencyKey)
				.map(OrderEntityMapper::jpaToDominio);
	}


	// La regla de "pedido abierto" la pone el adaptador: el servicio no tiene que pasar los estados cerrados.
	@Override
	public Optional<Order> findPedidoAbiertoDeMesa(Long tableId) {
		return springrepositoryorder.findByTable_TableIdAndStatusNotIn(tableId, Order.ESTADOS_CERRADOS)
				.map(OrderEntityMapper::jpaToDominio);
	}


	@Override
	public boolean tienePedidoAbierto(Long tableId) {
		return springrepositoryorder.existsByTable_TableIdAndStatusNotIn(tableId, Order.ESTADOS_CERRADOS);
	}


	@Override
	public List<Order> findByTenantIdAndStatusIn(UUID tenantId, Collection<OrderStatus> statuses) {
		if(statuses.isEmpty()) {
			return List.of();
		}
		return springrepositoryorder.findByTenantIdAndStatusInOrderByCreatedAtAsc(tenantId, statuses).stream()
				.map(OrderEntityMapper::jpaToDominio).toList();
	}


	@Override
	public int findMaxOrderNumberByTenantId(UUID tenantId) {
		return springrepositoryorder.findMaxOrderNumberByTenantId(tenantId);
	}


	// Prepara el MenuItemEntity de cada producto que aparece en las líneas, guardado por su id.
	// getReferenceById NO hace SELECT: devuelve un "proxy", una entidad vacía que solo conoce su id.
	// Para guardar una línea basta con eso, porque en la tabla order_items solo se escribe el id del producto
	// (la clave foránea menuitems_id). Con findById se haría un SELECT por producto que no necesitamos.
	// Si el mismo producto aparece en dos líneas, se pide una sola vez (containsKey).
	private Map<Long, MenuItemEntity> referenciasDeProductos(Order order) {
		Map<Long, MenuItemEntity> productos = new HashMap<>();
		for(OrderItem item : order.getItems()) {
			Long productoId = item.getMenuItemId();
			if(!productos.containsKey(productoId)) {
				MenuItemEntity referencia = springitemrepository.getReferenceById(productoId);
				productos.put(productoId, referencia);
			}
		}
		return productos;
	}


	// La tabla orders tiene tres UNIQUE que pueden saltar al guardar. Para saber cuál fue, se mira el nombre
	// de la restricción en el mensaje de PostgreSQL (la causa más profunda de la excepción).
	private RuntimeException traducirConflicto(DataIntegrityViolationException ex, Order order) {
		String causa = String.valueOf(ex.getMostSpecificCause().getMessage());
		if(causa.contains("uk_orders_mesa_abierta")) {
			return new MesaConPedidoAbiertoException("La mesa ya tiene un pedido abierto");
		}
		if(causa.contains("uk_order_tenant_number")) {
			return new OrderYaRegistradaException("El pedido " + order.getOrderNumber() + " ya existe, vuelve a intentarlo");
		}
		if(causa.contains("uk_orders_tenant_idempotency")) {
			return new OrderYaRegistradaException("Este pedido ya se ha registrado");
		}
		// Otra restricción (CHECK, clave foránea...): no es un conflicto de negocio conocido, se deja pasar.
		return ex;
	}

}
