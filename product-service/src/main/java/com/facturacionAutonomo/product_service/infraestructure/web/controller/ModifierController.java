package com.facturacionAutonomo.product_service.infraestructure.web.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.facturacionAutonomo.product_service.application.command.modifier.EliminarModifierCommand;
import com.facturacionAutonomo.product_service.application.command.modifier.RegistrarModifierCommand;
import com.facturacionAutonomo.product_service.application.command.modifier.UpdateModifierCommand;
import com.facturacionAutonomo.product_service.application.port.in.EliminarModifierUseCase;
import com.facturacionAutonomo.product_service.application.port.in.GetModifiersUseCase;
import com.facturacionAutonomo.product_service.application.port.in.RegistrarModifierUseCase;
import com.facturacionAutonomo.product_service.application.port.in.UpdateModifierUseCase;
import com.facturacionAutonomo.product_service.application.query.GetModifiersQuery;
import com.facturacionAutonomo.product_service.application.result.ModifierResult;
import com.facturacionAutonomo.product_service.infraestructure.utilitys.ApiResponse;
import com.facturacionAutonomo.product_service.infraestructure.utilitys.ResponseUtil;
import com.facturacionAutonomo.product_service.infraestructure.web.dto.ModifierRequest;
import com.facturacionAutonomo.product_service.infraestructure.web.dto.UpdateModifierRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/modifier")
public class ModifierController {

	private final RegistrarModifierUseCase registerModifiercase;

	private final GetModifiersUseCase getModifierscase;

	private final UpdateModifierUseCase updateModifiercase;

	private final EliminarModifierUseCase eliminarModifiercase;

	public ModifierController(RegistrarModifierUseCase registerModifiercase, GetModifiersUseCase getModifierscase,
			UpdateModifierUseCase updateModifiercase, EliminarModifierUseCase eliminarModifiercase) {
		super();
		this.registerModifiercase = registerModifiercase;
		this.getModifierscase = getModifierscase;
		this.updateModifiercase = updateModifiercase;
		this.eliminarModifiercase = eliminarModifiercase;
	}

	@GetMapping("/all")
	public ResponseEntity<ApiResponse<List<ModifierResult>>> getAllModifiers(
			@RequestHeader("X-Tenant-Id") UUID tenantId) {

		return ResponseEntity.status(HttpStatus.OK).body(
				ResponseUtil.sucess("La lista de los modificadores", getModifierscase.execute(new GetModifiersQuery(tenantId))));
	}

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<ModifierResult>> register(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@Valid @RequestBody ModifierRequest dto) {

		RegistrarModifierCommand command = new RegistrarModifierCommand(tenantId, dto.getName(), dto.getModifierType(),
				dto.getPriceDelta());
		return ResponseEntity.status(HttpStatus.CREATED).body(
				ResponseUtil.sucess("Modificador registrado correctamente", registerModifiercase.execute(command)));
	}

	@PutMapping("/{publicId}")
	public ResponseEntity<ApiResponse<ModifierResult>> update(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@PathVariable UUID publicId,
			@Valid @RequestBody UpdateModifierRequest dto) {

		UpdateModifierCommand command = new UpdateModifierCommand(tenantId, publicId, dto.getName(),
				dto.getModifierType(), dto.getPriceDelta(), dto.getActive());
		return ResponseEntity.status(HttpStatus.OK).body(
				ResponseUtil.sucess("Modificador actualizado correctamente", updateModifiercase.execute(command)));
	}

	@DeleteMapping("/{publicId}")
	public ResponseEntity<ApiResponse<Object>> delete(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@PathVariable UUID publicId) {

		eliminarModifiercase.execute(new EliminarModifierCommand(tenantId, publicId));
		return ResponseEntity.status(HttpStatus.OK).body(
				ResponseUtil.sucess("Modificador eliminado correctamente", null));
	}

}
