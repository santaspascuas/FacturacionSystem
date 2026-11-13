package com.facturacionAutonomo.product_service.application.result;

import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.MenuCategory;

public record CategoryResult(
		String name,
		UUID publicId,
		Integer displayOrder
		) {

	public static CategoryResult convertirA(MenuCategory categoria) {
		return new CategoryResult(categoria.getName(), categoria.getPublicId(), categoria.getDisplayOrder());
	}

}
