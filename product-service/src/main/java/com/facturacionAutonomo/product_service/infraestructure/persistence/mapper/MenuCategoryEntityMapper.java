package com.facturacionAutonomo.product_service.infraestructure.persistence.mapper;

import com.facturacionAutonomo.product_service.domain.model.MenuCategory;
import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.MenuCategoryEntity;

public class MenuCategoryEntityMapper {

	// Solo tiene métodos estáticos: no se instancia (igual que los mappers de customer-service y auth-service).
	private MenuCategoryEntityMapper() {}

	public static MenuCategoryEntity dominioToJpa(MenuCategory categoria) {
		MenuCategoryEntity crearEntidadjpa = new MenuCategoryEntity();
		crearEntidadjpa.setTenantId(categoria.getTenantId());
		crearEntidadjpa.setName(categoria.getName());
		crearEntidadjpa.setPublicId(categoria.getPublicId());
		crearEntidadjpa.setCreatedAt(categoria.getCreatedAt());
		crearEntidadjpa.setUpdatedAt(categoria.getUpdatedAt());
		crearEntidadjpa.setActive(categoria.isActive());
		crearEntidadjpa.setDisplayOrder(categoria.getDisplayOrder());
		return crearEntidadjpa;

	}

	public static MenuCategory jpaToDominio(MenuCategoryEntity categoryentidad) {

	return 	MenuCategory.restoreData(categoryentidad.getCategoryId(),
				categoryentidad.getTenantId(),
				categoryentidad.getPublicId(),
				categoryentidad.getName(),
				categoryentidad.getDisplayOrder(),
				categoryentidad.getActive(),
				categoryentidad.getCreatedAt(),
				categoryentidad.getUpdatedAt());

	}



}
