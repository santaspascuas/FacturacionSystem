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

import com.facturacionAutonomo.product_service.application.command.modifier.EliminarModifierCommand;
import com.facturacionAutonomo.product_service.application.command.modifier.RegistrarModifierCommand;
import com.facturacionAutonomo.product_service.application.command.modifier.UpdateModifierCommand;
import com.facturacionAutonomo.product_service.application.port.in.EliminarModifierUseCase;
import com.facturacionAutonomo.product_service.application.port.in.GetModifiersUseCase;
import com.facturacionAutonomo.product_service.application.port.in.RegistrarModifierUseCase;
import com.facturacionAutonomo.product_service.application.port.in.UpdateModifierUseCase;
import com.facturacionAutonomo.product_service.application.query.GetModifiersQuery;
import com.facturacionAutonomo.product_service.application.result.ModifierResult;
import com.facturacionAutonomo.product_service.domain.exception.ModifierNotFoundException;
import com.facturacionAutonomo.product_service.domain.exception.ModifierYaRegistradoException;
import com.facturacionAutonomo.product_service.domain.model.ModifierType;

@WebMvcTest(ModifierController.class)
class ModifierControllerTest {

	private static final String TENANT = "11111111-1111-1111-1111-111111111111";

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private RegistrarModifierUseCase registrar;
	@MockitoBean
	private GetModifiersUseCase listar;
	@MockitoBean
	private UpdateModifierUseCase modificar;
	@MockitoBean
	private EliminarModifierUseCase eliminar;

	private final UUID publicId = UUID.randomUUID();
	private final ModifierResult queso = new ModifierResult(publicId, "Extra de queso", ModifierType.ANADIR,
			new BigDecimal("1.50"), true);

	@Test
	void register_valido_responde201() throws Exception {
		when(registrar.execute(any(RegistrarModifierCommand.class))).thenReturn(queso);

		mockMvc.perform(post("/api/modifier/register").header("X-Tenant-Id", TENANT)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Extra de queso\",\"modifierType\":\"ANADIR\",\"priceDelta\":1.50}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data.publicId").value(publicId.toString()))
				.andExpect(jsonPath("$.data.modifierType").value("ANADIR"));
	}

	@Test
	void register_sinNombre_responde400SinLlamarAlCasoDeUso() throws Exception {
		mockMvc.perform(post("/api/modifier/register").header("X-Tenant-Id", TENANT)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\" \",\"modifierType\":\"ANADIR\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("El nombre del modificador es obligatorio"));
		verify(registrar, never()).execute(any());
	}

	@Test
	void register_tipoDesconocido_responde400() throws Exception {
		mockMvc.perform(post("/api/modifier/register").header("X-Tenant-Id", TENANT)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Extra de queso\",\"modifierType\":\"DOBLE\"}"))
				.andExpect(status().isBadRequest());
		verify(registrar, never()).execute(any());
	}

	@Test
	void register_precioConTresDecimales_responde400() throws Exception {
		mockMvc.perform(post("/api/modifier/register").header("X-Tenant-Id", TENANT)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Extra de queso\",\"modifierType\":\"ANADIR\",\"priceDelta\":1.505}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void register_sinCabeceraTenant_responde400() throws Exception {
		mockMvc.perform(post("/api/modifier/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Extra de queso\",\"modifierType\":\"ANADIR\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("La cabecera X-Tenant-Id es obligatoria"));
	}

	@Test
	void register_nombreRepetido_responde409() throws Exception {
		when(registrar.execute(any(RegistrarModifierCommand.class)))
				.thenThrow(new ModifierYaRegistradoException("El modificador Extra de queso ya está registrado"));

		mockMvc.perform(post("/api/modifier/register").header("X-Tenant-Id", TENANT)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Extra de queso\",\"modifierType\":\"ANADIR\"}"))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.errorCode").value(409));
	}

	@Test
	void getAll_responde200ConLaLista() throws Exception {
		when(listar.execute(new GetModifiersQuery(UUID.fromString(TENANT)))).thenReturn(List.of(queso));

		mockMvc.perform(get("/api/modifier/all").header("X-Tenant-Id", TENANT))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data[0].name").value("Extra de queso"));
	}

	@Test
	void update_valido_responde200() throws Exception {
		when(modificar.execute(any(UpdateModifierCommand.class))).thenReturn(queso);

		mockMvc.perform(put("/api/modifier/{publicId}", publicId).header("X-Tenant-Id", TENANT)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Extra de queso\",\"modifierType\":\"ANADIR\",\"priceDelta\":1.50,\"active\":true}"))
				.andExpect(status().isOk());
	}

	@Test
	void update_sinActive_responde400() throws Exception {
		mockMvc.perform(put("/api/modifier/{publicId}", publicId).header("X-Tenant-Id", TENANT)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Extra de queso\",\"modifierType\":\"ANADIR\",\"priceDelta\":1.50}"))
				.andExpect(status().isBadRequest());
		verify(modificar, never()).execute(any());
	}

	@Test
	void update_noEncontrado_responde404() throws Exception {
		when(modificar.execute(any(UpdateModifierCommand.class)))
				.thenThrow(new ModifierNotFoundException("El modificador no se ha encontrado"));

		mockMvc.perform(put("/api/modifier/{publicId}", publicId).header("X-Tenant-Id", TENANT)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"Extra de queso\",\"modifierType\":\"ANADIR\",\"priceDelta\":1.50,\"active\":true}"))
				.andExpect(status().isNotFound());
	}

	@Test
	void delete_responde200() throws Exception {
		mockMvc.perform(delete("/api/modifier/{publicId}", publicId).header("X-Tenant-Id", TENANT))
				.andExpect(status().isOk());
		verify(eliminar).execute(new EliminarModifierCommand(UUID.fromString(TENANT), publicId));
	}

	@Test
	void delete_noEncontrado_responde404() throws Exception {
		doThrow(new ModifierNotFoundException("El modificador no se ha encontrado"))
				.when(eliminar).execute(any(EliminarModifierCommand.class));

		mockMvc.perform(delete("/api/modifier/{publicId}", publicId).header("X-Tenant-Id", TENANT))
				.andExpect(status().isNotFound());
	}

	@Test
	void delete_publicIdNoUuid_responde400() throws Exception {
		mockMvc.perform(delete("/api/modifier/no-es-uuid").header("X-Tenant-Id", TENANT))
				.andExpect(status().isBadRequest());
	}

}
