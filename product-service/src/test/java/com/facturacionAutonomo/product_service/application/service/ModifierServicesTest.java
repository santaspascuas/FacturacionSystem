package com.facturacionAutonomo.product_service.application.service;

import static com.facturacionAutonomo.product_service.application.service.ModifierTestData.AHORA;
import static com.facturacionAutonomo.product_service.application.service.ModifierTestData.CLOCK;
import static com.facturacionAutonomo.product_service.application.service.ModifierTestData.TENANT;
import static com.facturacionAutonomo.product_service.application.service.ModifierTestData.modifier;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.facturacionAutonomo.product_service.application.command.modifier.EliminarModifierCommand;
import com.facturacionAutonomo.product_service.application.command.modifier.RegistrarModifierCommand;
import com.facturacionAutonomo.product_service.application.command.modifier.UpdateModifierCommand;
import com.facturacionAutonomo.product_service.application.port.OUT.MenuCategoryRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.OUT.MenuItemModifierRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.OUT.MenuItemRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.OUT.ModifierRepositoryPort;
import com.facturacionAutonomo.product_service.application.query.GetModifiersQuery;
import com.facturacionAutonomo.product_service.application.result.ModifierResult;
import com.facturacionAutonomo.product_service.application.service.finder.MenuItemFinder;
import com.facturacionAutonomo.product_service.application.service.finder.ModifierFinder;
import com.facturacionAutonomo.product_service.application.service.modificadores.EliminarModifierService;
import com.facturacionAutonomo.product_service.application.service.modificadores.GetModifiersService;
import com.facturacionAutonomo.product_service.application.service.modificadores.RegistrarModifierService;
import com.facturacionAutonomo.product_service.application.service.modificadores.UpdateModifierService;
import com.facturacionAutonomo.product_service.domain.exception.ModifierException;
import com.facturacionAutonomo.product_service.domain.exception.ModifierNotFoundException;
import com.facturacionAutonomo.product_service.domain.exception.ModifierYaRegistradoException;
import com.facturacionAutonomo.product_service.domain.model.Modifier;
import com.facturacionAutonomo.product_service.domain.model.ModifierType;

@ExtendWith(MockitoExtension.class)
class ModifierServicesTest {

	@Mock
	private ModifierRepositoryPort modifierrepository;
	@Mock
	private MenuItemRepositoryPort menuitemrepository;
	@Mock
	private MenuItemModifierRepositoryPort menuitemmodifierrepository;
	@Mock
	private MenuCategoryRepositoryPort menucategoryrepository;

	private ModifierFinder finder;

	@BeforeEach
	void setUp() {
		// Finders reales con repositorios falsos: así se prueba también la lógica de búsqueda.
		MenuItemFinder menuItemFinder = new MenuItemFinder(menuitemrepository, new CategoryFinder(menucategoryrepository));
		finder = new ModifierFinder(modifierrepository, menuItemFinder, menuitemmodifierrepository);
	}

	@Nested
	class Registrar {

		private RegistrarModifierService service;

		@BeforeEach
		void setUp() {
			service = new RegistrarModifierService(modifierrepository, finder, CLOCK);
		}

		@Test
		void guardaElModificadorConNombreSinEspaciosYPrecioCeroPorDefecto() {
			when(modifierrepository.existsByTenantIdAndName(TENANT, "Sin cebolla")).thenReturn(false);
			when(modifierrepository.save(any(Modifier.class))).thenAnswer(inv -> inv.getArgument(0));

			ModifierResult result = service.execute(
					new RegistrarModifierCommand(TENANT, "  Sin cebolla  ", ModifierType.ELIMINAR, null));

			ArgumentCaptor<Modifier> guardado = ArgumentCaptor.forClass(Modifier.class);
			verify(modifierrepository).save(guardado.capture());
			assertThat(guardado.getValue().getTenantId()).isEqualTo(TENANT);
			assertThat(guardado.getValue().getName()).isEqualTo("Sin cebolla");
			assertThat(guardado.getValue().getPriceDelta()).isEqualTo(new BigDecimal("0.00"));
			assertThat(guardado.getValue().getCreatedAt()).isEqualTo(AHORA);
			assertThat(result.active()).isTrue();
			assertThat(result.modifierType()).isEqualTo(ModifierType.ELIMINAR);
		}

		@Test
		void nombreYaRegistrado_lanzaModifierYaRegistradoYNoGuarda() {
			when(modifierrepository.existsByTenantIdAndName(TENANT, "Extra de queso")).thenReturn(true);

			assertThatThrownBy(() -> service.execute(
					new RegistrarModifierCommand(TENANT, "Extra de queso", ModifierType.ANADIR, BigDecimal.ONE)))
					.isInstanceOf(ModifierYaRegistradoException.class);
			verify(modifierrepository, never()).save(any());
		}

		@Test
		void sinTenant_lanzaModifierException() {
			assertThatThrownBy(() -> service.execute(
					new RegistrarModifierCommand(null, "Extra de queso", ModifierType.ANADIR, BigDecimal.ONE)))
					.isInstanceOf(ModifierException.class)
					.hasMessage("El tenant es obligatorio");
		}

		@Test
		void sinNombre_lanzaModifierExceptionSinConsultarLaBD() {
			assertThatThrownBy(() -> service.execute(
					new RegistrarModifierCommand(TENANT, " ", ModifierType.ANADIR, BigDecimal.ONE)))
					.isInstanceOf(ModifierException.class);
			verify(modifierrepository, never()).existsByTenantIdAndName(any(), anyString());
		}
	}

	@Nested
	class Listar {

		@Test
		void devuelveLosActivosDelTenant() {
			GetModifiersService service = new GetModifiersService(modifierrepository, finder);
			when(modifierrepository.findActiveByTenantId(TENANT)).thenReturn(List.of(modifier(1L, "Extra de queso", true)));

			List<ModifierResult> result = service.execute(new GetModifiersQuery(TENANT));

			assertThat(result).extracting(ModifierResult::name).containsExactly("Extra de queso");
		}
	}

	@Nested
	class Modificar {

		private UpdateModifierService service;

		@BeforeEach
		void setUp() {
			service = new UpdateModifierService(modifierrepository, finder, CLOCK);
		}

		@Test
		void mismoNombre_noCompruebaDuplicadoYActualizaTodo() {
			Modifier existente = modifier(1L, "Extra de queso", true);
			when(modifierrepository.findByTenantIdAndPublicId(TENANT, existente.getPublicId())).thenReturn(Optional.of(existente));
			when(modifierrepository.update(existente)).thenReturn(existente);

			ModifierResult result = service.execute(new UpdateModifierCommand(TENANT, existente.getPublicId(),
					"Extra de queso", ModifierType.SUSTITUIR, new BigDecimal("2.00"), true));

			verify(modifierrepository, never()).existsByTenantIdAndName(any(), anyString());
			assertThat(result.modifierType()).isEqualTo(ModifierType.SUSTITUIR);
			assertThat(result.priceDelta()).isEqualByComparingTo("2.00");
			assertThat(existente.getUpdatedAt()).isEqualTo(AHORA);
		}

		@Test
		void reactivaUnModificadorEliminado() {
			Modifier eliminado = modifier(1L, "Extra de queso", false);
			when(modifierrepository.findByTenantIdAndPublicId(TENANT, eliminado.getPublicId())).thenReturn(Optional.of(eliminado));
			when(modifierrepository.update(eliminado)).thenReturn(eliminado);

			ModifierResult result = service.execute(new UpdateModifierCommand(TENANT, eliminado.getPublicId(),
					"Extra de queso", ModifierType.ANADIR, new BigDecimal("1.50"), true));

			assertThat(result.active()).isTrue();
		}

		@Test
		void nombreNuevoOcupado_lanzaModifierYaRegistradoYNoActualiza() {
			Modifier existente = modifier(1L, "Extra de queso", true);
			when(modifierrepository.findByTenantIdAndPublicId(TENANT, existente.getPublicId())).thenReturn(Optional.of(existente));
			when(modifierrepository.existsByTenantIdAndName(TENANT, "Extra de bacon")).thenReturn(true);

			assertThatThrownBy(() -> service.execute(new UpdateModifierCommand(TENANT, existente.getPublicId(),
					"Extra de bacon", ModifierType.ANADIR, BigDecimal.ONE, true)))
					.isInstanceOf(ModifierYaRegistradoException.class);
			verify(modifierrepository, never()).update(any());
		}

		@Test
		void modificadorDeOtroTenant_lanzaModifierNotFound() {
			UUID publicId = UUID.randomUUID();
			when(modifierrepository.findByTenantIdAndPublicId(TENANT, publicId)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> service.execute(new UpdateModifierCommand(TENANT, publicId,
					"Extra de queso", ModifierType.ANADIR, BigDecimal.ONE, true)))
					.isInstanceOf(ModifierNotFoundException.class);
		}
	}

	@Nested
	class Eliminar {

		private EliminarModifierService service;

		@BeforeEach
		void setUp() {
			service = new EliminarModifierService(modifierrepository, finder, CLOCK);
		}

		@Test
		void loDejaInactivoYLoGuarda() {
			Modifier activo = modifier(1L, "Extra de queso", true);
			when(modifierrepository.findByTenantIdAndPublicId(TENANT, activo.getPublicId())).thenReturn(Optional.of(activo));

			service.execute(new EliminarModifierCommand(TENANT, activo.getPublicId()));

			verify(modifierrepository).update(activo);
			assertThat(activo.isActive()).isFalse();
			assertThat(activo.getUpdatedAt()).isEqualTo(AHORA);
		}

		@Test
		void yaEliminado_lanzaModifierNotFound() {
			Modifier eliminado = modifier(1L, "Extra de queso", false);
			when(modifierrepository.findByTenantIdAndPublicId(TENANT, eliminado.getPublicId())).thenReturn(Optional.of(eliminado));

			assertThatThrownBy(() -> service.execute(new EliminarModifierCommand(TENANT, eliminado.getPublicId())))
					.isInstanceOf(ModifierNotFoundException.class);
			verify(modifierrepository, never()).update(any());
		}
	}

}
