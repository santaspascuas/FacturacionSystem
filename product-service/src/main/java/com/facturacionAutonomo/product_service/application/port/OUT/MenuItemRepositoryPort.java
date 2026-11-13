package com.facturacionAutonomo.product_service.application.port.OUT;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.MenuItem;

public interface MenuItemRepositoryPort {

	MenuItem save(MenuItem item);

	// Guarda los cambios de un producto que ya existe (tiene id).
	MenuItem update(MenuItem item);

	// Si el tenant ya tiene un producto activo con ese nombre.
	boolean existsActiveByTenantIdAndName(UUID tenantId, String name);

	Optional<MenuItem> findByTenantIdAndPublicId(UUID tenantId, UUID publicId);

	// Productos activos del tenant, por nombre.
	List<MenuItem> findActiveByTenantId(UUID tenantId);

	// Productos activos de una categoría del tenant, por nombre.
	List<MenuItem> findActiveByTenantIdAndCategoryId(UUID tenantId, Long categoryId);

	// Varios productos activos del tenant en UNA consulta (las líneas de un pedido), en vez de uno por línea (N+1).
	// Los que no existen, son de otro tenant o están eliminados no vienen: el servicio lo detecta y responde 404.
	//busca metiendo en una coleccion los publicsids. saca todos.
	List<MenuItem> findActiveByTenantIdAndPublicIds(UUID tenantId, Collection<UUID> publicIds);

}
