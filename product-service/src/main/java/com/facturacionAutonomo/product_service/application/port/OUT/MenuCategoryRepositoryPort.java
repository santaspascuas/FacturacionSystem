package com.facturacionAutonomo.product_service.application.port.OUT;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.MenuCategory;

public interface MenuCategoryRepositoryPort {

	MenuCategory save(MenuCategory category);

	// Guarda los cambios de una categoría que ya existe (tiene id).
	MenuCategory update(MenuCategory category);

	boolean existsByTenantIdAndName(UUID tenantId, String name);

	// Última posición ocupada en la carta del tenant, o 0 si todavía no tiene categorías.
	int findMaxDisplayOrderByTenantId(UUID tenantId);

	// Categorías activas del tenant en el orden de la carta.
	List<MenuCategory> findActiveByTenantId(UUID tenantId);
	
	Optional<MenuCategory> findByTenantIdAndPublicId(UUID tenantId, UUID publicId);

}
