package com.facturacionAutonomo.product_service.infraestructure.persistence.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.Order;
import com.facturacionAutonomo.product_service.domain.model.OrderItem;
import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.MenuItemEntity;
import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.OrderEntity;
import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.OrderItemEntity;
import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.TablesEntity;

/**
 * Convierte el agregado pedido entero, Order <-> OrderEntity, con sus líneas.
 * Un solo mapper para el pedido y sus líneas: las líneas no existen sin su pedido, así que sus conversiones
 * son métodos privados de este mapper (nadie debería convertir una línea suelta).
 *
 * Es una clase Java pura: no accede a la BD. Lo que necesita de la BD (la mesa y los productos) se lo pasa
 * el adaptador, que es quien sabe cómo conseguirlo.
 */
public class OrderEntityMapper {

	// Solo tiene métodos estáticos: no se instancia (igual que el resto de mappers).
	private OrderEntityMapper() {}


	// ======================= Dominio -> JPA (para guardar un pedido nuevo) =======================

	/**
	 * @param mesa      la mesa del pedido, o null si es para LLEVAR.
	 * @param productos el MenuItemEntity de cada producto de las líneas, por su id. Lo prepara el adaptador
	 *                  (que es quien habla con la BD); el mapper solo lo consulta con productos.get(id).
	 *                  Así el mapper sigue siendo Java puro: no sabe de dónde salen las entidades.
	 */
	public static OrderEntity dominioToJpa(Order order, TablesEntity mesa, Map<Long, MenuItemEntity> productos) {
		OrderEntity entidad = new OrderEntity();
		// El id y el version no se copian: los ponen la secuencia y Hibernate al guardar.
		entidad.setPublicId(order.getPublicId());
		entidad.setTenantId(order.getTenantId());
		entidad.setOrderNumber(order.getOrderNumber());
		entidad.setOrderType(order.getOrderType());
		entidad.setStatus(order.getStatus());
		entidad.setTable(mesa);
		entidad.setNumPersonas(order.getNumPersonas());
		entidad.setSubtotal(order.getSubtotal());
		entidad.setTaxRate(order.getTaxRate());
		entidad.setTaxAmount(order.getTaxAmount());
		entidad.setTotal(order.getTotal());
		entidad.setPaidAmount(order.getPaidAmount());
		entidad.setPaidAt(order.getPaidAt());
		entidad.setCreatedAt(order.getCreatedAt());
		entidad.setUpdatedAt(order.getUpdatedAt());
		entidad.setCookedAt(order.getCookedAt());
		entidad.setIdempotencyKey(order.getIdempotencyKey());
		entidad.setClientTimestamp(order.getClientTimestamp());
		entidad.setSyncStatus(order.getSyncStatus());

		// ---- LA LISTA DE LÍNEAS ----
		// 1. order.getItems() devuelve una COPIA de solo lectura de las líneas del dominio (List.copyOf):
		//    se puede recorrer, pero no modificar. Aquí solo la recorremos.
		// 2. Cada OrderItem del dominio se convierte en un OrderItemEntity (itemToJpa).
		// 3. Se añade con entidad.addItem(...) y NO con entidad.getItems().add(...):
		//    - getItems() de la entidad es una vista de solo lectura (Collections.unmodifiableList):
		//      .add(...) lanzaría UnsupportedOperationException.
		//    - addItem hace dos cosas a la vez: mete la línea en la lista del pedido Y le pone a la línea
		//      su pedido (item.setOrder(this)). Ese segundo paso es el que rellena la clave foránea order_id:
		//      en JPA manda el lado propietario (OrderItemEntity.order); sin él se guardaría order_id = null.
		// 4. Como OrderEntity.items tiene cascade = ALL, al guardar el pedido se insertan también sus líneas:
		//    el adaptador hace un solo saveAndFlush.
		for(OrderItem item : order.getItems()) {
			MenuItemEntity producto = productos.get(item.getMenuItemId());
			OrderItemEntity linea = itemToJpa(item, producto);
			entidad.addItem(linea);
		}
		return entidad;
	}

	// La línea recibe el producto ya resuelto: el dominio solo guarda su id (menuItemId).
	private static OrderItemEntity itemToJpa(OrderItem item, MenuItemEntity producto) {
		OrderItemEntity linea = new OrderItemEntity();
		linea.setPublicId(item.getPublicId());
		linea.setMenuItem(producto);
		linea.setProductName(item.getProductName());
		linea.setUnitPrice(item.getUnitPrice());
		linea.setQuantity(item.getQuantity());
		linea.setLineTotal(item.getLineTotal());
		linea.setNotes(item.getNotes());
		linea.setIdempotencyKey(item.getIdempotencyKey());
		linea.setCreatedAt(item.getCreatedAt());
		// No se llama a setOrder: lo hace entidad.addItem(...) en dominioToJpa.
		return linea;
	}


	// ======================= Dominio -> JPA (para guardar cambios de un pedido existente) =======================

	/**
	 * Copia los cambios del pedido del dominio sobre la entidad que ya está en la BD (gestionada por Hibernate),
	 * y SINCRONIZA las líneas. No se crea una entidad nueva: así se conserva su @Version (bloqueo optimista)
	 * y Hibernate hace UPDATE en vez de INSERT.
	 */
	public static void actualizarJpa(OrderEntity entidad, Order order, Map<Long, MenuItemEntity> productos) {
		// Lo que puede cambiar de un pedido abierto. El tipo, la mesa, el número, el tenant y el tipo de IVA son fijos.
		entidad.setStatus(order.getStatus());
		entidad.setNumPersonas(order.getNumPersonas());
		entidad.setSubtotal(order.getSubtotal());
		entidad.setTaxAmount(order.getTaxAmount());
		entidad.setTotal(order.getTotal());
		entidad.setPaidAmount(order.getPaidAmount());
		entidad.setPaidAt(order.getPaidAt());
		entidad.setUpdatedAt(order.getUpdatedAt());
		entidad.setCookedAt(order.getCookedAt());
		entidad.setSyncStatus(order.getSyncStatus());

		// ---- SINCRONIZAR LA LISTA DE LÍNEAS ----
		// El dominio tiene la lista "buena" (la que debe quedar). La entidad tiene la que hay en la BD.
		// Se emparejan por publicId, que es el identificador de la línea que no cambia nunca.
		Map<UUID, OrderItem> enDominio = new HashMap<>();
		for(OrderItem item : order.getItems()) {
			enDominio.put(item.getPublicId(), item);
		}

		// 1. QUITAR las líneas que están en la BD pero ya no en el dominio (el camarero quitó un plato).
		//    Se recorre una COPIA (List.copyOf): no se puede quitar elementos de una lista mientras se recorre.
		//    removeItem la saca de la lista y le quita el pedido; con orphanRemoval = true, Hibernate hace
		//    DELETE de esa línea al guardar.
		for(OrderItemEntity linea : List.copyOf(entidad.getItems())) {
			if(!enDominio.containsKey(linea.getPublicId())) {
				entidad.removeItem(linea);
			}
		}

		// 2. Para cada línea del dominio: si ya estaba en la BD se ACTUALIZA (cantidad, total y notas son lo único
		//    que cambia; el producto y su precio son una foto fija); si no estaba se AÑADE con addItem
		//    (que le pone su pedido) y cascade = ALL hace el INSERT al guardar.
		Map<UUID, OrderItemEntity> enBd = new HashMap<>();
		for(OrderItemEntity linea : entidad.getItems()) {
			enBd.put(linea.getPublicId(), linea);
		}
		for(OrderItem item : order.getItems()) {
			OrderItemEntity linea = enBd.get(item.getPublicId());
			if(linea == null) {
				MenuItemEntity producto = productos.get(item.getMenuItemId());
				entidad.addItem(itemToJpa(item, producto));
			}else {
				linea.setQuantity(item.getQuantity());
				linea.setLineTotal(item.getLineTotal());
				linea.setNotes(item.getNotes());
			}
		}
	}


	// ======================= JPA -> Dominio (al leer un pedido) =======================

	/**
	 * Necesita las líneas YA cargadas: los find del repositorio llevan @EntityGraph(attributePaths = "items"),
	 * así que entidad.getItems() no lanza consultas. Sin @EntityGraph, leer las líneas de cada pedido
	 * lanzaría una consulta por pedido (N+1).
	 */
	public static Order jpaToDominio(OrderEntity entidad) {
		// ---- LA LISTA DE LÍNEAS ----
		// 1. entidad.getItems() es la vista de solo lectura de la entidad: solo se recorre.
		// 2. Cada OrderItemEntity se convierte en un OrderItem del dominio (itemToDominio).
		// 3. .toList() crea una lista nueva e inmutable. Se la pasamos a Order.restoreData, que hace su propia
		//    copia modificable interna (new ArrayList<>(items)): a partir de ahí, las líneas del pedido
		//    solo cambian con los métodos de Order (addItem, cambiarCantidad, quitarItem...), que recalculan el total.
		List<OrderItem> items = entidad.getItems().stream()
				.map(OrderEntityMapper::itemToDominio)
				.toList();

		// La mesa es LAZY: getTableId() de un proxy devuelve el id que ya tiene, sin consultar la tabla.
		Long tableId = entidad.getTable() != null ? entidad.getTable().getTableId() : null;

		return Order.restoreData(entidad.getOrderId(), entidad.getTenantId(), entidad.getPublicId(),
				entidad.getOrderNumber(), entidad.getOrderType(), entidad.getStatus(), tableId,
				entidad.getNumPersonas(), items, entidad.getSubtotal(), entidad.getTaxRate(), entidad.getTaxAmount(),
				entidad.getTotal(), entidad.getPaidAmount(), entidad.getPaidAt(), entidad.getCreatedAt(),
				entidad.getUpdatedAt(),
				entidad.getCookedAt(), entidad.getIdempotencyKey(), entidad.getClientTimestamp(),
				entidad.getSyncStatus());
	}

	// El producto también es LAZY: getMenuitemsId() del proxy no lanza consulta.
	private static OrderItem itemToDominio(OrderItemEntity linea) {
		return OrderItem.restoreData(linea.getOrderitemId(), linea.getPublicId(), linea.getMenuItem().getMenuitemsId(),
				linea.getProductName(), linea.getUnitPrice(), linea.getQuantity(), linea.getLineTotal(),
				linea.getNotes(), linea.getIdempotencyKey(), linea.getCreatedAt());
	}

}
