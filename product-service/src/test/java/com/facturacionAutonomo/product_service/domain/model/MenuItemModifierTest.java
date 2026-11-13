package com.facturacionAutonomo.product_service.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.facturacionAutonomo.product_service.domain.exception.ModifierException;

class MenuItemModifierTest {

	@Test
	void crearMenuItemModifier_guardaIdsYReglas() {
		MenuItemModifier asociacion = MenuItemModifier.crearMenuItemModifier(10L, 20L, 2, true);

		assertThat(asociacion.getId()).isNull();
		assertThat(asociacion.getMenuItemId()).isEqualTo(10L);
		assertThat(asociacion.getModifierId()).isEqualTo(20L);
		assertThat(asociacion.getMaxSelections()).isEqualTo(2);
		assertThat(asociacion.isRequired()).isTrue();
	}

	@Test
	void crearMenuItemModifier_maxSelectionsMenorQueUno_lanzaModifierException() {
		assertThatThrownBy(() -> MenuItemModifier.crearMenuItemModifier(10L, 20L, 0, false))
				.isInstanceOf(ModifierException.class);
	}

	@Test
	void updateRules_cambiaLasReglas() {
		MenuItemModifier asociacion = MenuItemModifier.restoreData(1L, 10L, 20L, 1, false);

		asociacion.updateRules(3, true);

		assertThat(asociacion.getMaxSelections()).isEqualTo(3);
		assertThat(asociacion.isRequired()).isTrue();
	}

	@Test
	void updateRules_maxSelectionsMenorQueUno_lanzaModifierExceptionYNoCambia() {
		MenuItemModifier asociacion = MenuItemModifier.restoreData(1L, 10L, 20L, 2, false);

		assertThatThrownBy(() -> asociacion.updateRules(0, true)).isInstanceOf(ModifierException.class);
		assertThat(asociacion.getMaxSelections()).isEqualTo(2);
		assertThat(asociacion.isRequired()).isFalse();
	}

}
