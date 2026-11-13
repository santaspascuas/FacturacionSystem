package com.facturacionAutonomo.product_service.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.facturacionAutonomo.product_service.domain.exception.ModifierException;

class ModifierTest {

	private static final UUID TENANT = UUID.randomUUID();
	private static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 7, 10, 0);

	@Test
	void crearModifier_naceActivoConPublicIdYNombreSinEspacios() {
		Modifier modifier = Modifier.crearModifier(TENANT, "  Extra de queso ", ModifierType.ANADIR,
				new BigDecimal("1.00"), AHORA);

		assertThat(modifier.getId()).isNull();
		assertThat(modifier.getPublicId()).isNotNull();
		assertThat(modifier.getName()).isEqualTo("Extra de queso");
		assertThat(modifier.isActive()).isTrue();
		assertThat(modifier.getCreatedAt()).isEqualTo(AHORA);
		assertThat(modifier.getUpdatedAt()).isEqualTo(AHORA);
	}

	@Test
	void crearModifier_sinNombre_lanzaModifierException() {
		assertThatThrownBy(() -> Modifier.crearModifier(TENANT, "   ", ModifierType.ANADIR, BigDecimal.ZERO, AHORA))
				.isInstanceOf(ModifierException.class)
				.hasMessage("El nombre del modificador es obligatorio");
	}

	@Test
	void crearModifier_sinTipo_lanzaModifierException() {
		assertThatThrownBy(() -> Modifier.crearModifier(TENANT, "Sin cebolla", null, BigDecimal.ZERO, AHORA))
				.isInstanceOf(ModifierException.class)
				.hasMessage("El tipo del modificador es obligatorio");
	}

	@Test
	void crearModifier_sinPrecio_lanzaModifierException() {
		assertThatThrownBy(() -> Modifier.crearModifier(TENANT, "Sin cebolla", ModifierType.ELIMINAR, null, AHORA))
				.isInstanceOf(ModifierException.class)
				.hasMessage("El precio del modificador es obligatorio");
	}

	@Test
	void changePrice_admitePrecioNegativo() {
		Modifier modifier = Modifier.crearModifier(TENANT, "Sin queso", ModifierType.ELIMINAR, BigDecimal.ZERO, AHORA);

		modifier.changePrice(new BigDecimal("-0.50"), AHORA.plusHours(1));

		assertThat(modifier.getPriceDelta()).isEqualByComparingTo("-0.50");
		assertThat(modifier.getUpdatedAt()).isEqualTo(AHORA.plusHours(1));
	}

	@Test
	void eliminar_loDejaInactivo() {
		Modifier modifier = Modifier.crearModifier(TENANT, "Sin sal", ModifierType.ELIMINAR, BigDecimal.ZERO, AHORA);

		modifier.eliminar(AHORA.plusHours(1));

		assertThat(modifier.isActive()).isFalse();
		assertThat(modifier.getUpdatedAt()).isEqualTo(AHORA.plusHours(1));
	}

}
