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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.facturacionAutonomo.product_service.application.command.menuitem.EliminarMenuItemCommand;
import com.facturacionAutonomo.product_service.application.command.menuitem.RegistrarMenuItemCommand;
import com.facturacionAutonomo.product_service.application.command.menuitem.UpdateRegistrarMenuItemCommand;
import com.facturacionAutonomo.product_service.application.port.in.UpdateMenuItemUseCase;
import com.facturacionAutonomo.product_service.infraestructure.web.dto.UpdateMenuItemRequest;
import com.facturacionAutonomo.product_service.application.port.in.EliminarMenuItemUseCase;
import com.facturacionAutonomo.product_service.application.port.in.GetMenuItemsUseCase;
import com.facturacionAutonomo.product_service.application.port.in.RegistrarMenuItemUseCase;
import com.facturacionAutonomo.product_service.application.query.GetMenuItemsQuery;
import com.facturacionAutonomo.product_service.application.result.MenuItemResult;
import com.facturacionAutonomo.product_service.infraestructure.utilitys.ApiResponse;
import com.facturacionAutonomo.product_service.infraestructure.utilitys.ResponseUtil;
import com.facturacionAutonomo.product_service.infraestructure.web.dto.MenuItemRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/menuitem")
public class MenuItemController {

	private final RegistrarMenuItemUseCase registerMenuItemcase;

	private final GetMenuItemsUseCase getMenuItemscase;

	private final EliminarMenuItemUseCase eliminarMenuItemcase;

	private final UpdateMenuItemUseCase updateMenuItemcase;

	public MenuItemController(RegistrarMenuItemUseCase registerMenuItemcase, GetMenuItemsUseCase getMenuItemscase,
			EliminarMenuItemUseCase eliminarMenuItemcase, UpdateMenuItemUseCase updateMenuItemcase) {
		super();
		this.registerMenuItemcase = registerMenuItemcase;
		this.getMenuItemscase = getMenuItemscase;
		this.eliminarMenuItemcase = eliminarMenuItemcase;
		this.updateMenuItemcase = updateMenuItemcase;
	}

	// ?categoryPublicId= es opcional: sin él se lista toda la carta del tenant.
	@GetMapping("/all")
	public ResponseEntity<ApiResponse<List<MenuItemResult>>> getAllMenuItems(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@RequestParam(required = false) UUID categoryPublicId) {

		GetMenuItemsQuery query = new GetMenuItemsQuery(tenantId, categoryPublicId);
		return ResponseEntity.status(HttpStatus.OK).body(
				ResponseUtil.sucess("La lista de los productos", getMenuItemscase.execute(query)));
	}

	@DeleteMapping("/{publicId}")
	public ResponseEntity<ApiResponse<Object>> delete(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@PathVariable UUID publicId) {

		eliminarMenuItemcase.execute(new EliminarMenuItemCommand(tenantId, publicId));
		return ResponseEntity.status(HttpStatus.OK).body(
				ResponseUtil.sucess("Producto eliminado correctamente", null));
	}

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<MenuItemResult>> register(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@Valid @RequestBody MenuItemRequest dto) {

		RegistrarMenuItemCommand command = new RegistrarMenuItemCommand(tenantId, dto.getCategoryPublicId(),
				dto.getName(), dto.getDescription(), dto.getBasePrice(), dto.getImageUrl());
		return ResponseEntity.status(HttpStatus.CREATED).body(
				ResponseUtil.sucess("Producto registrado correctamente", registerMenuItemcase.execute(command)));
	}
	
	@PutMapping("/{publicId}")
	public ResponseEntity<ApiResponse<MenuItemResult>> updateItems(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@PathVariable UUID publicId,
			@Valid @RequestBody UpdateMenuItemRequest dto){
		UpdateRegistrarMenuItemCommand command = new UpdateRegistrarMenuItemCommand(tenantId, publicId,
				dto.getCategoryPublicId(), dto.getName(), dto.getDescription(), dto.getBasePrice(), dto.getImageUrl(),
				dto.getActive());

		return ResponseEntity.status(HttpStatus.OK).body(
				ResponseUtil.sucess("Se ha actualizado el item consumido", updateMenuItemcase.execute(command)));
	}
	
	
	
	
	

}
