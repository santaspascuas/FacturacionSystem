package com.facturacionAutonomo.product_service.application.result;

import java.math.BigDecimal;
import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.MenuItem;

public record MenuItemResult(
		UUID publicId,
		String name,
		String description,
		BigDecimal basePrice,
		boolean active,
		String imageUrl
		) {

	public static MenuItemResult convertirA(MenuItem item) {
		return new MenuItemResult(item.getPublicId(), item.getName(), item.getDescription(), item.getBasePrice(),
				item.isActive(), item.getImageUrl());
	}

}
