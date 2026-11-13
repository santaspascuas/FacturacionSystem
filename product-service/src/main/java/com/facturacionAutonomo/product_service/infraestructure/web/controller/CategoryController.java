package com.facturacionAutonomo.product_service.infraestructure.web.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.facturacionAutonomo.product_service.application.command.category.RegistrarCategoryCommand;
import com.facturacionAutonomo.product_service.application.command.category.UpdateCategoryCommand;
import com.facturacionAutonomo.product_service.application.port.in.GetCategoriesUseCase;
import com.facturacionAutonomo.product_service.application.port.in.RegistarCategoryUseCase;
import com.facturacionAutonomo.product_service.application.port.in.UpdateCategoryUseCase;
import com.facturacionAutonomo.product_service.application.query.GetCategoriesQuery;
import com.facturacionAutonomo.product_service.application.result.CategoryResult;
import com.facturacionAutonomo.product_service.infraestructure.utilitys.ApiResponse;
import com.facturacionAutonomo.product_service.infraestructure.utilitys.ResponseUtil;
import com.facturacionAutonomo.product_service.infraestructure.web.dto.CategoryRequest;
import com.facturacionAutonomo.product_service.infraestructure.web.dto.UpdateCategoryRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

	private final RegistarCategoryUseCase registerCategorycase;
	
	private final GetCategoriesUseCase getCategorycase;

	private final UpdateCategoryUseCase updateCategorycase;

	public CategoryController(RegistarCategoryUseCase registerCategorycase, GetCategoriesUseCase getCategorycase,
			UpdateCategoryUseCase updateCategorycase) {
		super();
		this.registerCategorycase = registerCategorycase;
		this.getCategorycase = getCategorycase;
		this.updateCategorycase = updateCategorycase;
	}

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<CategoryResult>> register(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@Valid @RequestBody CategoryRequest dto) {

		RegistrarCategoryCommand command = new RegistrarCategoryCommand(tenantId, dto.getName());
		return ResponseEntity.status(HttpStatus.CREATED).body(
				ResponseUtil.sucess("Categoría registrada correctamente", registerCategorycase.execute(command)));
	}
	
	@GetMapping("/all")
	public ResponseEntity<ApiResponse<List<CategoryResult>>> getallCategory(
			@RequestHeader("X-Tenant-Id") UUID tenantId	
			){
		GetCategoriesQuery command = new GetCategoriesQuery(tenantId);
		return ResponseEntity.status(HttpStatus.OK).body(
				ResponseUtil.sucess("Se lista las categorias", getCategorycase.execute(command)));
	}

	@PutMapping("/{publicId}")
	public ResponseEntity<ApiResponse<CategoryResult>> update(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@PathVariable UUID publicId,
			@Valid @RequestBody UpdateCategoryRequest dto) {

		UpdateCategoryCommand command = new UpdateCategoryCommand(tenantId, publicId, dto.getName(), dto.getActive());
		return ResponseEntity.status(HttpStatus.OK).body(
				ResponseUtil.sucess("Categoría actualizada correctamente", updateCategorycase.execute(command)));
	}
	
	
	

}
