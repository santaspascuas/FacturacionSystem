package com.facturacionAutonomo.product_service.application.service.finder;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.facturacionAutonomo.product_service.application.port.OUT.MenuItemRepositoryPort;
import com.facturacionAutonomo.product_service.application.service.CategoryFinder;
import com.facturacionAutonomo.product_service.domain.exception.MenuItemException;
import com.facturacionAutonomo.product_service.domain.exception.MenuItemNotFoundException;
import com.facturacionAutonomo.product_service.domain.model.MenuCategory;
import com.facturacionAutonomo.product_service.domain.model.MenuItem;

/**
 * Búsquedas que comparten los casos de uso de productos (y los de modificadores, a través de ModifierFinder).
 * Filtran por tenant: un producto de otro tenant, para este tenant no existe.
 * Las excepciones son las de producto (MenuItemException), como antes de centralizarlas.
 */
@Component
public class MenuItemFinder {

	private final MenuItemRepositoryPort menuitemrepository;
	private final CategoryFinder categoryfinder;


	public MenuItemFinder(MenuItemRepositoryPort menuitemrepository, CategoryFinder categoryfinder) {
		super();
		this.menuitemrepository = menuitemrepository;
		this.categoryfinder = categoryfinder;
	}


	public void tenantObligatorio(UUID tenantId) {
		if(tenantId == null) {
			throw new MenuItemException("El tenant es obligatorio");
		}
	}

	// Incluye los inactivos: modificar es la forma de volver a activar un producto eliminado.
	public MenuItem productoDelTenant(UUID tenantId, UUID publicId) {
		if(publicId == null) {
			throw new MenuItemException("El producto es obligatorio");
		}
		return menuitemrepository.findByTenantIdAndPublicId(tenantId, publicId)
				.orElseThrow(() -> new MenuItemNotFoundException("El producto no se ha encontrado"));
	}

	// Un producto eliminado cuenta como no encontrado, igual que uno de otro tenant.
	public MenuItem productoActivo(UUID tenantId, UUID publicId) {
		MenuItem item = productoDelTenant(tenantId, publicId);
		if(!item.isActive()) {
			throw new MenuItemNotFoundException("El producto no se ha encontrado");
		}
		return item;
	}

	// La categoría en la que queda un producto: obligatoria y del mismo tenant.
	public MenuCategory categoriaDelProducto(UUID tenantId, UUID categoryPublicId) {
		if(categoryPublicId == null) {
			throw new MenuItemException("La categoría del producto es obligatoria");
		}
		return categoryfinder.categoriaDelTenant(tenantId, categoryPublicId);
	}

}
