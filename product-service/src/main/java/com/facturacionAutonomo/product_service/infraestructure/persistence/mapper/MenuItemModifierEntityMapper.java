package com.facturacionAutonomo.product_service.infraestructure.persistence.mapper;

import com.facturacionAutonomo.product_service.domain.model.MenuItemModifier;
import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.MenuItemEntity;
import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.MenuItemModifierEntity;
import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.ModifierEntity;

public class MenuItemModifierEntityMapper {

	// Solo tiene métodos estáticos: no se instancia (igual que los mappers de customer-service y auth-service).
	private MenuItemModifierEntityMapper() {}

	// El producto y el modificador los pone el adaptador: el dominio solo conoce sus ids.
	public static MenuItemModifierEntity dominioToJpa(MenuItemModifier menuItemModifier, MenuItemEntity menuItem,
			ModifierEntity modifier) {
		MenuItemModifierEntity crearjpa = new MenuItemModifierEntity();
		crearjpa.setMenuItem(menuItem);
		crearjpa.setModifier(modifier);
		crearjpa.setMaxSelections(menuItemModifier.getMaxSelections());
		crearjpa.setRequired(menuItemModifier.isRequired());
		return crearjpa;
	}

	// Los getters de id de los proxies LAZY no lanzan consulta: el id ya lo tiene el proxy.
	public static MenuItemModifier jpaToDominio(MenuItemModifierEntity entity) {
		return MenuItemModifier.restoreData(entity.getMenuItemModifierId(),
				entity.getMenuItem().getMenuitemsId(),
				entity.getModifier().getModificadorId(),
				entity.getMaxSelections(), entity.getRequired());
	}

}
