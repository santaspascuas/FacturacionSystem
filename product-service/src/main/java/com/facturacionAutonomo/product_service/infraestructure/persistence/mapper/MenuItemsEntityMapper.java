package com.facturacionAutonomo.product_service.infraestructure.persistence.mapper;

import com.facturacionAutonomo.product_service.domain.model.MenuItem;
import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.MenuCategoryEntity;
import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.MenuItemEntity;

public class MenuItemsEntityMapper {

	// Solo tiene métodos estáticos: no se instancia (igual que los mappers de customer-service y auth-service).
	private MenuItemsEntityMapper() {}

	// La categoría la pone el adaptador: el dominio solo conoce su id.
	public static MenuItemEntity dominiotoJpa(MenuItem menu, MenuCategoryEntity category) {

		 MenuItemEntity crearjpa = new MenuItemEntity();
		 crearjpa.setTenantId(menu.getTenantId());
		 crearjpa.setActive(menu.isActive());
		 crearjpa.setPublicId(menu.getPublicId());
		 crearjpa.setName(menu.getName());
		 crearjpa.setDescription(menu.getDescription());
		 crearjpa.setBasePrice(menu.getBasePrice());
		 crearjpa.setCreatedAt(menu.getCreatedAt());
		 crearjpa.setUpdatedAt(menu.getUpdatedAt());
		 crearjpa.setImageUrl(menu.getImageUrl());
		 crearjpa.setCategory(category);
		return crearjpa;
	}

	// getCategoryId() de un proxy LAZY no lanza consulta: el id ya lo tiene el proxy.
	public static MenuItem jpaToDominio(MenuItemEntity item) {

		return MenuItem.restoreData(item.getMenuitemsId(), item.getTenantId(),
				item.getPublicId(), item.getCategory().getCategoryId(),
				item.getName(), item.getDescription(),
				item.getBasePrice(), item.getActive(),
				item.getImageUrl(), item.getCreatedAt(), item.getUpdatedAt());
	}


}
