package com.facturacionAutonomo.product_service.infraestructure.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.facturacionAutonomo.product_service.application.command.menuitemmodifier.AsignarModifierCommand;
import com.facturacionAutonomo.product_service.application.command.menuitemmodifier.QuitarModifierCommand;
import com.facturacionAutonomo.product_service.application.command.menuitemmodifier.UpdateMenuItemModifierCommand;
import com.facturacionAutonomo.product_service.application.port.in.AsignarModifierUseCase;
import com.facturacionAutonomo.product_service.application.port.in.GetMenuItemModifiersUseCase;
import com.facturacionAutonomo.product_service.application.port.in.QuitarModifierUseCase;
import com.facturacionAutonomo.product_service.application.port.in.UpdateMenuItemModifierUseCase;
import com.facturacionAutonomo.product_service.application.query.GetMenuItemModifiersQuery;
import com.facturacionAutonomo.product_service.application.result.MenuItemModifierResult;
import com.facturacionAutonomo.product_service.domain.exception.MenuItemNotFoundException;
import com.facturacionAutonomo.product_service.domain.exception.ModifierNoAsignadoException;
import com.facturacionAutonomo.product_service.domain.exception.ModifierYaAsignadoException;
import com.facturacionAutonomo.product_service.domain.model.ModifierType;

@WebMvcTest(MenuItemModifierController.class)
class MenuItemModifierControllerTest {

	private static final String TENANT = "11111111-1111-1111-1111-111111111111";

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AsignarModifierUseCase asignar;
	@MockitoBean
	private GetMenuItemModifiersUseCase listar;
	@MockitoBean
	private UpdateMenuItemModifierUseCase modificar;
	@MockitoBean
	private QuitarModifierUseCase quitar;

	private final UUID itemId = UUID.randomUUID();
	private final UUID modifierId = UUID.randomUUID();
	private final MenuItemModifierResult queso = new MenuItemModifierResult(modifierId, "Extra de queso",
			ModifierType.ANADIR, new BigDecimal("1.50"), 1, false);

	@Test
	void asignar_soloConModificador_responde201() throws Exception {
		when(asignar.execute(any(AsignarModifierCommand.class))).thenReturn(queso);

		mockMvc.perform(post("/api/menuitem/{item}/modifiers", itemId).header("X-Tenant-Id", TENANT)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"modifierPublicId\":\"" + modifierId + "\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.data.modifierPublicId").value(modifierId.toString()))
				.andExpect(jsonPath("$.data.maxSelections").value(1));
		verify(asignar).execute(new AsignarModifierCommand(UUID.fromString(TENANT), itemId, modifierId, null, null));
	}

	@Test
	void asignar_sinModificador_responde400() throws Exception {
		mockMvc.perform(post("/api/menuitem/{item}/modifiers", itemId).header("X-Tenant-Id", TENANT)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("El modificador es obligatorio"));
		verify(asignar, never()).execute(any());
	}

	@Test
	void asignar_maxSelectionsCero_responde400() throws Exception {
		mockMvc.perform(post("/api/menuitem/{item}/modifiers", itemId).header("X-Tenant-Id", TENANT)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"modifierPublicId\":\"" + modifierId + "\",\"maxSelections\":0}"))
				.andExpect(status().isBadRequest());
		verify(asignar, never()).execute(any());
	}

	@Test
	void asignar_yaAsignado_responde409() throws Exception {
		when(asignar.execute(any(AsignarModifierCommand.class)))
				.thenThrow(new ModifierYaAsignadoException("El modificador Extra de queso ya está asociado a este producto"));

		mockMvc.perform(post("/api/menuitem/{item}/modifiers", itemId).header("X-Tenant-Id", TENANT)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"modifierPublicId\":\"" + modifierId + "\"}"))
				.andExpect(status().isConflict());
	}

	@Test
	void asignar_productoNoEncontrado_responde404() throws Exception {
		when(asignar.execute(any(AsignarModifierCommand.class)))
				.thenThrow(new MenuItemNotFoundException("El producto no se ha encontrado"));

		mockMvc.perform(post("/api/menuitem/{item}/modifiers", itemId).header("X-Tenant-Id", TENANT)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"modifierPublicId\":\"" + modifierId + "\"}"))
				.andExpect(status().isNotFound());
	}

	@Test
	void listar_responde200() throws Exception {
		when(listar.execute(new GetMenuItemModifiersQuery(UUID.fromString(TENANT), itemId))).thenReturn(List.of(queso));

		mockMvc.perform(get("/api/menuitem/{item}/modifiers", itemId).header("X-Tenant-Id", TENANT))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data[0].name").value("Extra de queso"));
	}

	@Test
	void modificar_valido_responde200() throws Exception {
		when(modificar.execute(any(UpdateMenuItemModifierCommand.class))).thenReturn(queso);

		mockMvc.perform(put("/api/menuitem/{item}/modifiers/{mod}", itemId, modifierId).header("X-Tenant-Id", TENANT)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"maxSelections\":2,\"required\":true}"))
				.andExpect(status().isOk());
		verify(modificar).execute(new UpdateMenuItemModifierCommand(UUID.fromString(TENANT), itemId, modifierId, 2, true));
	}

	@Test
	void modificar_sinRequired_responde400() throws Exception {
		mockMvc.perform(put("/api/menuitem/{item}/modifiers/{mod}", itemId, modifierId).header("X-Tenant-Id", TENANT)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"maxSelections\":2}"))
				.andExpect(status().isBadRequest());
		verify(modificar, never()).execute(any());
	}

	@Test
	void modificar_noAsignado_responde404() throws Exception {
		when(modificar.execute(any(UpdateMenuItemModifierCommand.class)))
				.thenThrow(new ModifierNoAsignadoException("El modificador no está asociado a este producto"));

		mockMvc.perform(put("/api/menuitem/{item}/modifiers/{mod}", itemId, modifierId).header("X-Tenant-Id", TENANT)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"maxSelections\":2,\"required\":true}"))
				.andExpect(status().isNotFound());
	}

	@Test
	void quitar_responde200() throws Exception {
		mockMvc.perform(delete("/api/menuitem/{item}/modifiers/{mod}", itemId, modifierId).header("X-Tenant-Id", TENANT))
				.andExpect(status().isOk());
		verify(quitar).execute(new QuitarModifierCommand(UUID.fromString(TENANT), itemId, modifierId));
	}

	@Test
	void quitar_noAsignado_responde404() throws Exception {
		doThrow(new ModifierNoAsignadoException("El modificador no está asociado a este producto"))
				.when(quitar).execute(any(QuitarModifierCommand.class));

		mockMvc.perform(delete("/api/menuitem/{item}/modifiers/{mod}", itemId, modifierId).header("X-Tenant-Id", TENANT))
				.andExpect(status().isNotFound());
	}

}
