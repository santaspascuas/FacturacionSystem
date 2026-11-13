package com.facturacionAutonomo.product_service.application.service;

import static com.facturacionAutonomo.product_service.application.service.ModifierTestData.TENANT;
import static com.facturacionAutonomo.product_service.application.service.ModifierTestData.asociacion;
import static com.facturacionAutonomo.product_service.application.service.ModifierTestData.modifier;
import static com.facturacionAutonomo.product_service.application.service.ModifierTestData.producto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.facturacionAutonomo.product_service.application.command.menuitemmodifier.AsignarModifierCommand;
import com.facturacionAutonomo.product_service.application.command.menuitemmodifier.QuitarModifierCommand;
import com.facturacionAutonomo.product_service.application.command.menuitemmodifier.UpdateMenuItemModifierCommand;
import com.facturacionAutonomo.product_service.application.port.OUT.MenuCategoryRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.OUT.MenuItemModifierRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.OUT.MenuItemRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.OUT.ModifierRepositoryPort;
import com.facturacionAutonomo.product_service.application.query.GetMenuItemModifiersQuery;
import com.facturacionAutonomo.product_service.application.result.MenuItemModifierResult;
import com.facturacionAutonomo.product_service.application.service.finder.MenuItemFinder;
import com.facturacionAutonomo.product_service.application.service.finder.ModifierFinder;
import com.facturacionAutonomo.product_service.application.service.modificadores.AsignarModifierService;
import com.facturacionAutonomo.product_service.application.service.modificadores.GetMenuItemModifiersService;
import com.facturacionAutonomo.product_service.application.service.modificadores.QuitarModifierService;
import com.facturacionAutonomo.product_service.application.service.modificadores.UpdateMenuItemModifierService;
import com.facturacionAutonomo.product_service.domain.exception.MenuItemNotFoundException;
import com.facturacionAutonomo.product_service.domain.exception.ModifierException;
import com.facturacionAutonomo.product_service.domain.exception.ModifierNoAsignadoException;
import com.facturacionAutonomo.product_service.domain.exception.ModifierNotFoundException;
import com.facturacionAutonomo.product_service.domain.exception.ModifierYaAsignadoException;
import com.facturacionAutonomo.product_service.domain.model.MenuItem;
import com.facturacionAutonomo.product_service.domain.model.MenuItemModifier;
import com.facturacionAutonomo.product_service.domain.model.Modifier;

@ExtendWith(MockitoExtension.class)
class MenuItemModifierServicesTest {

	@Mock
	private ModifierRepositoryPort modifierrepository;
	@Mock
	private MenuItemRepositoryPort menuitemrepository;
	@Mock
	private MenuItemModifierRepositoryPort menuitemmodifierrepository;
	@Mock
	private MenuCategoryRepositoryPort menucategoryrepository;

	private ModifierFinder finder;

	private MenuItem item;
	private Modifier queso;

	@BeforeEach
	void setUp() {
		MenuItemFinder menuItemFinder = new MenuItemFinder(menuitemrepository, new CategoryFinder(menucategoryrepository));
		finder = new ModifierFinder(modifierrepository, menuItemFinder, menuitemmodifierrepository);
		item = producto(10L, true);
		queso = modifier(20L, "Extra de queso", true);
	}

	private void existeProducto(MenuItem producto) {
		when(menuitemrepository.findByTenantIdAndPublicId(TENANT, producto.getPublicId())).thenReturn(Optional.of(producto));
	}

	private void existeModifier(Modifier modifier) {
		when(modifierrepository.findByTenantIdAndPublicId(TENANT, modifier.getPublicId())).thenReturn(Optional.of(modifier));
	}

	@Nested
	class Asignar {

		private AsignarModifierService service;

		@BeforeEach
		void setUp() {
			service = new AsignarModifierService(menuitemmodifierrepository, finder);
		}

		@Test
		void sinReglas_usaLosValoresPorDefectoDeLaBD() {
			existeProducto(item);
			existeModifier(queso);
			when(menuitemmodifierrepository.existsByMenuItemIdAndModifierId(10L, 20L)).thenReturn(false);
			when(menuitemmodifierrepository.save(any(MenuItemModifier.class))).thenAnswer(inv -> inv.getArgument(0));

			MenuItemModifierResult result = service.execute(
					new AsignarModifierCommand(TENANT, item.getPublicId(), queso.getPublicId(), null, null));

			ArgumentCaptor<MenuItemModifier> guardada = ArgumentCaptor.forClass(MenuItemModifier.class);
			verify(menuitemmodifierrepository).save(guardada.capture());
			assertThat(guardada.getValue().getMenuItemId()).isEqualTo(10L);
			assertThat(guardada.getValue().getModifierId()).isEqualTo(20L);
			assertThat(guardada.getValue().getMaxSelections()).isEqualTo(1);
			assertThat(guardada.getValue().isRequired()).isFalse();
			assertThat(result.modifierPublicId()).isEqualTo(queso.getPublicId());
			assertThat(result.name()).isEqualTo("Extra de queso");
		}

		@Test
		void conReglas_lasGuarda() {
			existeProducto(item);
			existeModifier(queso);
			when(menuitemmodifierrepository.save(any(MenuItemModifier.class))).thenAnswer(inv -> inv.getArgument(0));

			MenuItemModifierResult result = service.execute(
					new AsignarModifierCommand(TENANT, item.getPublicId(), queso.getPublicId(), 3, true));

			assertThat(result.maxSelections()).isEqualTo(3);
			assertThat(result.required()).isTrue();
		}

		@Test
		void yaAsignado_lanzaModifierYaAsignadoYNoGuarda() {
			existeProducto(item);
			existeModifier(queso);
			when(menuitemmodifierrepository.existsByMenuItemIdAndModifierId(10L, 20L)).thenReturn(true);

			assertThatThrownBy(() -> service.execute(
					new AsignarModifierCommand(TENANT, item.getPublicId(), queso.getPublicId(), null, null)))
					.isInstanceOf(ModifierYaAsignadoException.class);
			verify(menuitemmodifierrepository, never()).save(any());
		}

		@Test
		void productoEliminado_lanzaMenuItemNotFound() {
			MenuItem eliminado = producto(10L, false);
			existeProducto(eliminado);

			assertThatThrownBy(() -> service.execute(
					new AsignarModifierCommand(TENANT, eliminado.getPublicId(), queso.getPublicId(), null, null)))
					.isInstanceOf(MenuItemNotFoundException.class);
		}

		@Test
		void modificadorDeOtroTenant_lanzaModifierNotFound() {
			existeProducto(item);
			UUID deOtroTenant = UUID.randomUUID();
			when(modifierrepository.findByTenantIdAndPublicId(TENANT, deOtroTenant)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> service.execute(
					new AsignarModifierCommand(TENANT, item.getPublicId(), deOtroTenant, null, null)))
					.isInstanceOf(ModifierNotFoundException.class);
			verify(menuitemmodifierrepository, never()).save(any());
		}

		@Test
		void maxSelectionsCero_lanzaModifierException() {
			existeProducto(item);
			existeModifier(queso);

			assertThatThrownBy(() -> service.execute(
					new AsignarModifierCommand(TENANT, item.getPublicId(), queso.getPublicId(), 0, false)))
					.isInstanceOf(ModifierException.class);
			verify(menuitemmodifierrepository, never()).save(any());
		}
	}

	@Nested
	class Listar {

		@Test
		void cargaLosModificadoresEnUnaConsultaYOmiteLosEliminados() {
			GetMenuItemModifiersService service = new GetMenuItemModifiersService(menuitemmodifierrepository,
					modifierrepository, finder);
			Modifier eliminado = modifier(21L, "Sin cebolla", false);
			existeProducto(item);
			when(menuitemmodifierrepository.findByMenuItemId(10L))
					.thenReturn(List.of(asociacion(1L, 10L, 20L), asociacion(2L, 10L, 21L)));
			when(modifierrepository.findByIds(List.of(20L, 21L))).thenReturn(List.of(queso, eliminado));

			List<MenuItemModifierResult> result = service.execute(new GetMenuItemModifiersQuery(TENANT, item.getPublicId()));

			assertThat(result).extracting(MenuItemModifierResult::name).containsExactly("Extra de queso");
			verify(modifierrepository, never()).findByTenantIdAndPublicId(any(), any());
		}
	}

	@Nested
	class Modificar {

		private UpdateMenuItemModifierService service;

		@BeforeEach
		void setUp() {
			service = new UpdateMenuItemModifierService(menuitemmodifierrepository, finder);
		}

		@Test
		void cambiaLasReglas() {
			MenuItemModifier asociacion = asociacion(1L, 10L, 20L);
			existeProducto(item);
			existeModifier(queso);
			when(menuitemmodifierrepository.findByMenuItemIdAndModifierId(10L, 20L)).thenReturn(Optional.of(asociacion));
			when(menuitemmodifierrepository.update(asociacion)).thenReturn(asociacion);

			MenuItemModifierResult result = service.execute(
					new UpdateMenuItemModifierCommand(TENANT, item.getPublicId(), queso.getPublicId(), 2, true));

			assertThat(result.maxSelections()).isEqualTo(2);
			assertThat(result.required()).isTrue();
		}

		@Test
		void noAsignado_lanzaModifierNoAsignado() {
			existeProducto(item);
			existeModifier(queso);
			when(menuitemmodifierrepository.findByMenuItemIdAndModifierId(10L, 20L)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> service.execute(
					new UpdateMenuItemModifierCommand(TENANT, item.getPublicId(), queso.getPublicId(), 2, true)))
					.isInstanceOf(ModifierNoAsignadoException.class);
			verify(menuitemmodifierrepository, never()).update(any());
		}
	}

	@Nested
	class Quitar {

		private QuitarModifierService service;

		@BeforeEach
		void setUp() {
			service = new QuitarModifierService(menuitemmodifierrepository, finder);
		}

		@Test
		void borraLaAsociacionAunqueElModificadorEsteEliminado() {
			Modifier eliminado = modifier(20L, "Extra de queso", false);
			existeProducto(item);
			existeModifier(eliminado);
			when(menuitemmodifierrepository.findByMenuItemIdAndModifierId(10L, 20L))
					.thenReturn(Optional.of(asociacion(1L, 10L, 20L)));

			service.execute(new QuitarModifierCommand(TENANT, item.getPublicId(), eliminado.getPublicId()));

			verify(menuitemmodifierrepository).deleteById(1L);
		}

		@Test
		void noAsignado_lanzaModifierNoAsignadoYNoBorra() {
			existeProducto(item);
			existeModifier(queso);
			when(menuitemmodifierrepository.findByMenuItemIdAndModifierId(10L, 20L)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> service.execute(
					new QuitarModifierCommand(TENANT, item.getPublicId(), queso.getPublicId())))
					.isInstanceOf(ModifierNoAsignadoException.class);
			verify(menuitemmodifierrepository, never()).deleteById(anyLong());
		}
	}

}
