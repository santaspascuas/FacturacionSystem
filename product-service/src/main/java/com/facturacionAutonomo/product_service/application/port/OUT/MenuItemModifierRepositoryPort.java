package com.facturacionAutonomo.product_service.application.port.OUT;

import java.util.List;
import java.util.Optional;

import com.facturacionAutonomo.product_service.domain.model.MenuItemModifier;

public interface MenuItemModifierRepositoryPort {

	MenuItemModifier save(MenuItemModifier menuItemModifier);

	// Guarda los cambios de reglas (maxSelections, required) de una asociación que ya existe.
	MenuItemModifier update(MenuItemModifier menuItemModifier);

	boolean existsByMenuItemIdAndModifierId(Long menuItemId, Long modifierId);

	Optional<MenuItemModifier> findByMenuItemIdAndModifierId(Long menuItemId, Long modifierId);

	// Modificadores que admite un producto.
	List<MenuItemModifier> findByMenuItemId(Long menuItemId);

	// Quitar un modificador de un producto es un borrado físico: la asociación no tiene histórico.
	void deleteById(Long id);

}
