package com.facturacionAutonomo.product_service.application.port.OUT;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.Order;
import com.facturacionAutonomo.product_service.domain.model.OrderStatus;

// Puerto de salida de los pedidos. Habla solo de dominio y en lenguaje de negocio:
// la regla de "pedido abierto" (Order.ESTADOS_CERRADOS) la aplica el adaptador, no el servicio.
// Sin delete: un pedido no se borra nunca (es historial de facturación); se anula con el estado CANCELADO.
public interface OrderRepositoryPort {

	Order save(Order order);

	// Guarda los cambios del pedido Y sincroniza sus líneas (nuevas, modificadas y quitadas).
	Order update(Order order);

	// Todos los pedidos del tenant, del más reciente al más antiguo.
	List<Order> findByTenantId(UUID tenantId);

	Optional<Order> findByTenantIdAndPublicId(UUID tenantId, UUID publicId);

	// Si el TPV reintenta enviar un pedido, se devuelve el que ya existe.
	Optional<Order> findByTenantIdAndIdempotencyKey(UUID tenantId, String idempotencyKey);

	// "Una mesa, una orden": el pedido abierto de la mesa, si lo tiene.
	// tableId es interno: el servicio lo obtiene tras buscar la mesa por tenant, así que ya es de este restaurante.
	Optional<Order> findPedidoAbiertoDeMesa(Long tableId);

	// Para responder 409 antes de abrir un segundo pedido en la misma mesa.
	boolean tienePedidoAbierto(Long tableId);

	// Cola de cocina: pedidos del tenant en esos estados, del más antiguo al más nuevo.
	List<Order> findByTenantIdAndStatusIn(UUID tenantId, Collection<OrderStatus> statuses);

	// Último número de pedido del tenant (0 si todavía no tiene): el siguiente es este + 1.
	int findMaxOrderNumberByTenantId(UUID tenantId);

}
