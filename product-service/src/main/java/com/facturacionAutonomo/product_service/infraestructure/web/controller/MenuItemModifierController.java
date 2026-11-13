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

import com.facturacionAutonomo.product_service.application.command.menuitemmodifier.AsignarModifierCommand;
import com.facturacionAutonomo.product_service.application.command.menuitemmodifier.QuitarModifierCommand;
import com.facturacionAutonomo.product_service.application.command.menuitemmodifier.UpdateMenuItemModifierCommand;
import com.facturacionAutonomo.product_service.application.port.in.AsignarModifierUseCase;
import com.facturacionAutonomo.product_service.application.port.in.GetMenuItemModifiersUseCase;
import com.facturacionAutonomo.product_service.application.port.in.QuitarModifierUseCase;
import com.facturacionAutonomo.product_service.application.port.in.UpdateMenuItemModifierUseCase;
import com.facturacionAutonomo.product_service.application.query.GetMenuItemModifiersQuery;
import com.facturacionAutonomo.product_service.application.result.MenuItemModifierResult;
import com.facturacionAutonomo.product_service.infraestructure.utilitys.ApiResponse;
import com.facturacionAutonomo.product_service.infraestructure.utilitys.ResponseUtil;
import com.facturacionAutonomo.product_service.infraestructure.web.dto.MenuItemModifierRequest;
import com.facturacionAutonomo.product_service.infraestructure.web.dto.UpdateMenuItemModifierRequest;

import jakarta.validation.Valid;

// Los modificadores que admite cada producto. El producto y el modificador se identifican por su publicId.
@RestController
@RequestMapping("/api/menuitem/{menuItemPublicId}/modifiers")
public class MenuItemModifierController {

	private final AsignarModifierUseCase asignarModifiercase;

	private final GetMenuItemModifiersUseCase getMenuItemModifierscase;

	private final UpdateMenuItemModifierUseCase updateMenuItemModifiercase;

	private final QuitarModifierUseCase quitarModifiercase;

	public MenuItemModifierController(AsignarModifierUseCase asignarModifiercase,
			GetMenuItemModifiersUseCase getMenuItemModifierscase,
			UpdateMenuItemModifierUseCase updateMenuItemModifiercase, QuitarModifierUseCase quitarModifiercase) {
		super();
		this.asignarModifiercase = asignarModifiercase;
		this.getMenuItemModifierscase = getMenuItemModifierscase;
		this.updateMenuItemModifiercase = updateMenuItemModifiercase;
		this.quitarModifiercase = quitarModifiercase;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<MenuItemModifierResult>>> getModifiers(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@PathVariable UUID menuItemPublicId) {

		GetMenuItemModifiersQuery query = new GetMenuItemModifiersQuery(tenantId, menuItemPublicId);
		return ResponseEntity.status(HttpStatus.OK).body(
				ResponseUtil.sucess("Los modificadores del producto" + menuItemPublicId, getMenuItemModifierscase.execute(query)));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<MenuItemModifierResult>> asignar(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@PathVariable UUID menuItemPublicId,
			@Valid @RequestBody MenuItemModifierRequest dto) {

		AsignarModifierCommand command = new AsignarModifierCommand(tenantId, menuItemPublicId,
				dto.getModifierPublicId(), dto.getMaxSelections(), dto.getRequired());
		return ResponseEntity.status(HttpStatus.CREATED).body(
				ResponseUtil.sucess("Modificador asociado al producto", asignarModifiercase.execute(command)));
	}

	@PutMapping("/{modifierPublicId}")
	public ResponseEntity<ApiResponse<MenuItemModifierResult>> update(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@PathVariable UUID menuItemPublicId,
			@PathVariable UUID modifierPublicId,
			@Valid @RequestBody UpdateMenuItemModifierRequest dto) {

		UpdateMenuItemModifierCommand command = new UpdateMenuItemModifierCommand(tenantId, menuItemPublicId,
				modifierPublicId, dto.getMaxSelections(), dto.getRequired());
		return ResponseEntity.status(HttpStatus.OK).body(
				ResponseUtil.sucess("Modificador del producto actualizado", updateMenuItemModifiercase.execute(command)));
	}

	@DeleteMapping("/{modifierPublicId}")
	public ResponseEntity<ApiResponse<Object>> quitar(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@PathVariable UUID menuItemPublicId,
			@PathVariable UUID modifierPublicId) {

		quitarModifiercase.execute(new QuitarModifierCommand(tenantId, menuItemPublicId, modifierPublicId));
		return ResponseEntity.status(HttpStatus.OK).body(
				ResponseUtil.sucess("Modificador quitado del producto", null));
	}

}
