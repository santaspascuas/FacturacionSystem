package com.facturacionAutonomo.product_service.application.service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.MenuCategory;
import com.facturacionAutonomo.product_service.domain.model.MenuItem;
import com.facturacionAutonomo.product_service.domain.model.MenuItemModifier;
import com.facturacionAutonomo.product_service.domain.model.Modifier;
import com.facturacionAutonomo.product_service.domain.model.ModifierType;

// Datos comunes de las pruebas de los casos de uso del catálogo (categorías, productos y modificadores).
final class ModifierTestData {

	static final UUID TENANT = UUID.fromString("11111111-1111-1111-1111-111111111111");
	static final Clock CLOCK = Clock.fixed(Instant.parse("2026-11-07T10:00:00Z"), ZoneOffset.UTC);
	static final LocalDateTime AHORA = LocalDateTime.now(CLOCK);
	static final LocalDateTime ANTES = AHORA.minusDays(1);

	private ModifierTestData() {}

	static Modifier modifier(Long id, String name, boolean active) {
		return Modifier.restoreData(id, TENANT, UUID.randomUUID(), name, ModifierType.ANADIR,
				new BigDecimal("1.50"), active, ANTES, ANTES);
	}

	static MenuItem producto(Long id, boolean active) {
		return MenuItem.restoreData(id, TENANT, UUID.randomUUID(), 1L, "Hamburguesa", null,
				new BigDecimal("9.90"), active, null, ANTES, ANTES);
	}

	static MenuCategory categoria(Long id, String name) {
		return MenuCategory.restoreData(id, TENANT, UUID.randomUUID(), name, 1, true, ANTES, ANTES);
	}

	static MenuItem producto(Long id, String name, boolean active) {
		return MenuItem.restoreData(id, TENANT, UUID.randomUUID(), 1L, name, null,
				new BigDecimal("9.90"), active, null, ANTES, ANTES);
	}

	static MenuItemModifier asociacion(Long id, Long menuItemId, Long modifierId) {
		return MenuItemModifier.restoreData(id, menuItemId, modifierId, 1, false);
	}

}
