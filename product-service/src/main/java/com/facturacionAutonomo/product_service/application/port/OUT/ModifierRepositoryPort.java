package com.facturacionAutonomo.product_service.application.port.OUT;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.Modifier;

public interface ModifierRepositoryPort {

	Modifier save(Modifier modifier);

	// Guarda los cambios de un modificador que ya existe (tiene id).
	Modifier update(Modifier modifier);

	// El UNIQUE (tenant_id, name) incluye los inactivos, así que se comprueba contra todos.
	boolean existsByTenantIdAndName(UUID tenantId, String name);

	Optional<Modifier> findByTenantIdAndPublicId(UUID tenantId, UUID publicId);

	// Modificadores activos del tenant, por nombre.
	List<Modifier> findActiveByTenantId(UUID tenantId);

	// Carga varios modificadores en una sola consulta (evita el N+1 al listar los de un producto).
	List<Modifier> findByIds(Collection<Long> ids);

}
