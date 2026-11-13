package com.facturacionAutonomo.product_service.application.service;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.facturacionAutonomo.product_service.application.port.OUT.MenuCategoryRepositoryPort;
import com.facturacionAutonomo.product_service.domain.exception.CategoryException;
import com.facturacionAutonomo.product_service.domain.exception.CategoryNotFoundException;
import com.facturacionAutonomo.product_service.domain.model.MenuCategory;

/**
 * Búsquedas que comparten los casos de uso de categorías y de productos.
 * Filtran por tenant: una categoría de otro tenant, para este tenant no existe.
 */
@Component
public class CategoryFinder {

	private final MenuCategoryRepositoryPort menucategoryrepository;


	public CategoryFinder(MenuCategoryRepositoryPort menucategoryrepository) {
		super();
		this.menucategoryrepository = menucategoryrepository;
	}


	public void tenantObligatorio(UUID tenantId) {
		if(tenantId == null) {
			throw new CategoryException("El tenant es obligatorio");
		}
	}

	// Incluye las inactivas: modificar es la forma de volver a activar una categoría.
	public MenuCategory categoriaDelTenant(UUID tenantId, UUID publicId) {
		return menucategoryrepository.findByTenantIdAndPublicId(tenantId, publicId)
				.orElseThrow(() -> new CategoryNotFoundException("La categoría no se ha encontrado"));
	}

}
