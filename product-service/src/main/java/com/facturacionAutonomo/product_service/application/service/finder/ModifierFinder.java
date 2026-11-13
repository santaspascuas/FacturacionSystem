package com.facturacionAutonomo.product_service.application.service.finder;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.facturacionAutonomo.product_service.application.port.OUT.MenuItemModifierRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.OUT.ModifierRepositoryPort;
import com.facturacionAutonomo.product_service.domain.exception.ModifierException;
import com.facturacionAutonomo.product_service.domain.exception.ModifierNoAsignadoException;
import com.facturacionAutonomo.product_service.domain.exception.ModifierNotFoundException;
import com.facturacionAutonomo.product_service.domain.model.MenuItem;
import com.facturacionAutonomo.product_service.domain.model.MenuItemModifier;
import com.facturacionAutonomo.product_service.domain.model.Modifier;

/**
 * Búsquedas que comparten los casos de uso de modificadores. Todas filtran por tenant:
 * un producto o modificador de otro tenant, para este tenant no existe.
 */
@Component
public class ModifierFinder {

	private final ModifierRepositoryPort modifierrepository;
	private final MenuItemFinder menuitemfinder;
	private final MenuItemModifierRepositoryPort menuitemmodifierrepository;


	public ModifierFinder(ModifierRepositoryPort modifierrepository, MenuItemFinder menuitemfinder,
			MenuItemModifierRepositoryPort menuitemmodifierrepository) {
		super();
		this.modifierrepository = modifierrepository;
		this.menuitemfinder = menuitemfinder;
		this.menuitemmodifierrepository = menuitemmodifierrepository;
	}


	public void tenantObligatorio(UUID tenantId) {
		if(tenantId == null) {
			throw new ModifierException("El tenant es obligatorio");
		}
	}

	// Incluye los inactivos: modificar es la forma de volver a activar un modificador eliminado.
	public Modifier modifierDelTenant(UUID tenantId, UUID publicId) {
		if(publicId == null) {
			throw new ModifierException("El modificador es obligatorio");
		}
		return modifierrepository.findByTenantIdAndPublicId(tenantId, publicId)
				.orElseThrow(() -> new ModifierNotFoundException("El modificador no se ha encontrado"));
	}

	// Un modificador eliminado cuenta como no encontrado.
	public Modifier modifierActivo(UUID tenantId, UUID publicId) {
		Modifier modifier = modifierDelTenant(tenantId, publicId);
		if(!modifier.isActive()) {
			throw new ModifierNotFoundException("El modificador no se ha encontrado");
		}
		return modifier;
	}

	// La búsqueda del producto vive en MenuItemFinder; aquí solo se delega para no repetirla.
	public MenuItem productoActivo(UUID tenantId, UUID publicId) {
		return menuitemfinder.productoActivo(tenantId, publicId);
	}

	public MenuItemModifier asociacion(Long menuItemId, Long modifierId) {
		return menuitemmodifierrepository.findByMenuItemIdAndModifierId(menuItemId, modifierId)
				.orElseThrow(() -> new ModifierNoAsignadoException("El modificador no está asociado a este producto"));
	}

}
