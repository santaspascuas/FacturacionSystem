package com.facturacionAutonomo.product_service.infraestructure.web.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.facturacionAutonomo.product_service.application.command.tables.CambiarEstadoTablesCommand;
import com.facturacionAutonomo.product_service.application.command.tables.EliminarTablesCommand;
import com.facturacionAutonomo.product_service.application.command.tables.RegistrarTablesCommand;
import com.facturacionAutonomo.product_service.application.command.tables.UpdateTablesCommand;
import com.facturacionAutonomo.product_service.application.port.in.tables.CambiarEstadoTablesUseCase;
import com.facturacionAutonomo.product_service.application.port.in.tables.EliminarTablesUseCase;
import com.facturacionAutonomo.product_service.application.port.in.tables.GetTablesUseCase;
import com.facturacionAutonomo.product_service.application.port.in.tables.RegistrarTablesUseCase;
import com.facturacionAutonomo.product_service.application.port.in.tables.UpdateTablesUseCase;
import com.facturacionAutonomo.product_service.application.query.GetTablesQuery;
import com.facturacionAutonomo.product_service.application.result.TablesResult;
import com.facturacionAutonomo.product_service.domain.model.TableStatus;
import com.facturacionAutonomo.product_service.infraestructure.utilitys.ApiResponse;
import com.facturacionAutonomo.product_service.infraestructure.utilitys.ResponseUtil;
import com.facturacionAutonomo.product_service.infraestructure.web.dto.CambiarEstadoTablesRequest;
import com.facturacionAutonomo.product_service.infraestructure.web.dto.TablesRequest;
import com.facturacionAutonomo.product_service.infraestructure.web.dto.UpdateTablesRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/table")
public class TablesController {

	private final RegistrarTablesUseCase registrarTablecase;

	private final UpdateTablesUseCase updateTablecase;

	private final CambiarEstadoTablesUseCase cambiarEstadoTablecase;

	private final GetTablesUseCase getTablescase;

	private final EliminarTablesUseCase eliminarTablecase;

	public TablesController(RegistrarTablesUseCase registrarTablecase, UpdateTablesUseCase updateTablecase,
			CambiarEstadoTablesUseCase cambiarEstadoTablecase, GetTablesUseCase getTablescase,
			EliminarTablesUseCase eliminarTablecase) {
		super();
		this.registrarTablecase = registrarTablecase;
		this.updateTablecase = updateTablecase;
		this.cambiarEstadoTablecase = cambiarEstadoTablecase;
		this.getTablescase = getTablescase;
		this.eliminarTablecase = eliminarTablecase;
	}

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<TablesResult>> register(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@Valid @RequestBody TablesRequest dto) {

		RegistrarTablesCommand command = new RegistrarTablesCommand(tenantId, dto.getTableNumber(),
				dto.getCapacity(), dto.getZone());
		return ResponseEntity.status(HttpStatus.CREATED).body(
				ResponseUtil.sucess("Mesa registrada correctamente", registrarTablecase.execute(command)));
	}

	// ?status= es opcional: sin él se listan todas las mesas; con ?status=DISPONIBLE, solo las libres.
	@GetMapping("/all")
	public ResponseEntity<ApiResponse<List<TablesResult>>> getAll(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@RequestParam(required = false) TableStatus status) {

		return ResponseEntity.status(HttpStatus.OK).body(
				ResponseUtil.sucess("La lista de las mesas", getTablescase.execute(new GetTablesQuery(tenantId, status))));
	}

	// PUT: reemplaza los datos de la mesa (número, capacidad y zona).
	@PutMapping("/{publicId}")
	public ResponseEntity<ApiResponse<TablesResult>> update(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@PathVariable UUID publicId,
			@Valid @RequestBody UpdateTablesRequest dto) {

		UpdateTablesCommand command = new UpdateTablesCommand(tenantId, publicId, dto.getTableNumber(),
				dto.getCapacity(), dto.getZone());
		return ResponseEntity.status(HttpStatus.OK).body(
				ResponseUtil.sucess("Mesa actualizada correctamente", updateTablecase.execute(command)));
	}

	// PATCH: cambia solo una parte de la mesa, su estado.
	@PatchMapping("/{publicId}/status")
	public ResponseEntity<ApiResponse<TablesResult>> cambiarEstado(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@PathVariable UUID publicId,
			@Valid @RequestBody CambiarEstadoTablesRequest dto) {

		CambiarEstadoTablesCommand command = new CambiarEstadoTablesCommand(tenantId, publicId, dto.getStatus());
		return ResponseEntity.status(HttpStatus.OK).body(
				ResponseUtil.sucess("Estado de la mesa actualizado", cambiarEstadoTablecase.execute(command)));
	}

	@DeleteMapping("/{publicId}")
	public ResponseEntity<ApiResponse<Object>> delete(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@PathVariable UUID publicId) {

		eliminarTablecase.execute(new EliminarTablesCommand(tenantId, publicId));
		return ResponseEntity.status(HttpStatus.OK).body(
				ResponseUtil.sucess("Mesa eliminada correctamente", null));
	}

}
