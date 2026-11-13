package com.facturacionAutonomo.product_service.infraestructure.persistence.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.facturacionAutonomo.product_service.domain.model.OrderStatus;
import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.OrderEntity;

/**
 * Pedidos de cada tenant. Los métodos que devuelven pedidos completos llevan @EntityGraph(attributePaths = "items"):
 * Hibernate trae el pedido y sus líneas en UNA consulta (LEFT JOIN FETCH), en vez de una consulta más por pedido
 * al leer las líneas (N+1). LEFT y no INNER: un pedido recién abierto todavía no tiene líneas y tiene que salir igual.
 * Los parámetros "cerrados" reciben Order.ESTADOS_CERRADOS (CIERRAYPAGA y CANCELADO).
 */
public interface SpringDataOrderEntityRepository extends JpaRepository<OrderEntity, Long> {

	// Todos los pedidos del tenant, del más reciente al más antiguo, con sus líneas.
	@EntityGraph(attributePaths = "items")
	List<OrderEntity> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);

	// Abrir un pedido para verlo o modificarlo: el agregado entero, con sus líneas.
	@EntityGraph(attributePaths = "items")
	Optional<OrderEntity> findByTenantIdAndPublicId(UUID tenantId, UUID publicId);

	// Si el TPV reintenta enviar un pedido (p. ej. tras estar sin conexión), se devuelve el que ya existe.
	@EntityGraph(attributePaths = "items")
	Optional<OrderEntity> findByTenantIdAndIdempotencyKey(UUID tenantId, String idempotencyKey);

	// "Una mesa, una orden": el pedido abierto de una mesa. Como mucho hay uno (índice uk_orders_mesa_abierta).
	// Table_TableId navega la relación table hasta su id. El tableId es interno y ya pertenece al tenant
	// (la mesa se buscó antes filtrando por tenant).
	@EntityGraph(attributePaths = "items")
	Optional<OrderEntity> findByTable_TableIdAndStatusNotIn(Long tableId, Collection<OrderStatus> cerrados);

	// Para comprobar antes de abrir un pedido que la mesa no tiene ya uno abierto (409 con mensaje claro).
	boolean existsByTable_TableIdAndStatusNotIn(Long tableId, Collection<OrderStatus> cerrados);

	// Cola de cocina: los pedidos en varios estados (ENVIADO, COMANDALANZADA, PREPARANDOCOMIDA...),
	// del más antiguo al más nuevo, con sus líneas.
	@EntityGraph(attributePaths = "items")
	List<OrderEntity> findByTenantIdAndStatusInOrderByCreatedAtAsc(UUID tenantId, Collection<OrderStatus> statuses);

	// Siguiente número de pedido del tenant: este valor + 1 (0 si todavía no tiene pedidos).
	@Query("SELECT COALESCE(MAX(o.orderNumber), 0) FROM OrderEntity o WHERE o.tenantId = :tenantId")
	int findMaxOrderNumberByTenantId(@Param("tenantId") UUID tenantId);

}
