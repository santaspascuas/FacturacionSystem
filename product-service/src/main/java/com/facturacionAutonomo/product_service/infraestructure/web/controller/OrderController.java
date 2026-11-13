package com.facturacionAutonomo.product_service.infraestructure.web.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.facturacionAutonomo.product_service.application.command.orders.CambiarEstadoOrderCommand;
import com.facturacionAutonomo.product_service.application.command.orders.LineaOrderCommand;
import com.facturacionAutonomo.product_service.application.command.orders.RegistrarOrderCommand;
import com.facturacionAutonomo.product_service.application.port.in.order.CambiarEstadoOrderUseCase;
import com.facturacionAutonomo.product_service.application.port.in.order.GetOrderUseCase;
import com.facturacionAutonomo.product_service.application.port.in.order.GetOrdersUseCase;
import com.facturacionAutonomo.product_service.application.port.in.order.RegistrarOrderUseCase;
import com.facturacionAutonomo.product_service.application.query.GetOrderQuery;
import com.facturacionAutonomo.product_service.application.query.GetOrdersQuery;
import com.facturacionAutonomo.product_service.application.result.OrderResult;
import com.facturacionAutonomo.product_service.infraestructure.utilitys.ApiResponse;
import com.facturacionAutonomo.product_service.infraestructure.utilitys.ResponseUtil;
import com.facturacionAutonomo.product_service.infraestructure.web.dto.CambiarEstadoOrderRequest;
import com.facturacionAutonomo.product_service.infraestructure.web.dto.RegistrarOrderRequest;

import jakarta.validation.Valid;

// Sin DELETE: un pedido no se borra nunca (es historial de facturación); se anula con el estado CANCELADO.
@RestController
@RequestMapping("/api/order")
public class OrderController {

	private final GetOrdersUseCase getOrderscase;

	private final GetOrderUseCase getOrdercase;

	private final RegistrarOrderUseCase registrarOrdercase;

	private final CambiarEstadoOrderUseCase cambiarEstadoOrdercase;

	public OrderController(GetOrdersUseCase getOrderscase, GetOrderUseCase getOrdercase,
			RegistrarOrderUseCase registrarOrdercase, CambiarEstadoOrderUseCase cambiarEstadoOrdercase) {
		super();
		this.getOrderscase = getOrderscase;
		this.getOrdercase = getOrdercase;
		this.registrarOrdercase = registrarOrdercase;
		this.cambiarEstadoOrdercase = cambiarEstadoOrdercase;
	}

	// 1. Todos los pedidos del restaurante, del más reciente al más antiguo.
	@GetMapping("/all")
	public ResponseEntity<ApiResponse<List<OrderResult>>> getAll(
			@RequestHeader("X-Tenant-Id") UUID tenantId) {

		return ResponseEntity.status(HttpStatus.OK).body(
				ResponseUtil.sucess("La lista de los pedidos", getOrderscase.execute(new GetOrdersQuery(tenantId, false))));
	}

	// 2. Un pedido con sus líneas.
	@GetMapping("/{publicId}")
	public ResponseEntity<ApiResponse<OrderResult>> getOne(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@PathVariable UUID publicId) {

		return ResponseEntity.status(HttpStatus.OK).body(
				ResponseUtil.sucess("El pedido", getOrdercase.execute(new GetOrderQuery(tenantId, publicId))));
	}

	// 3. Crear un pedido con sus líneas.
	@PostMapping("/register")
	public ResponseEntity<ApiResponse<OrderResult>> register(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@Valid @RequestBody RegistrarOrderRequest dto) {

		List<LineaOrderCommand> lineas = dto.getLineas().stream()
				.map(linea -> new LineaOrderCommand(linea.getMenuItemPublicId(), linea.getQuantity(), linea.getNotes()))
				.toList();
		RegistrarOrderCommand command = new RegistrarOrderCommand(tenantId, dto.getOrderType(), dto.getTablePublicId(),
				dto.getNumPersonas(), dto.getIdempotencyKey(), dto.getClientTimestamp(), lineas);
		return ResponseEntity.status(HttpStatus.CREATED).body(
				ResponseUtil.sucess("Pedido registrado correctamente", registrarOrdercase.execute(command)));
	}

	// 4. Cambiar el estado: avanzar en cocina, cerrar y cobrar (CIERRAYPAGA) o anular (CANCELADO).
	@PatchMapping("/{publicId}/status")
	public ResponseEntity<ApiResponse<OrderResult>> cambiarEstado(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@PathVariable UUID publicId,
			@Valid @RequestBody CambiarEstadoOrderRequest dto) {

		CambiarEstadoOrderCommand command = new CambiarEstadoOrderCommand(tenantId, publicId, dto.getStatus());
		return ResponseEntity.status(HttpStatus.OK).body(
				ResponseUtil.sucess("Estado del pedido actualizado", cambiarEstadoOrdercase.execute(command)));
	}

	// 5. Pedidos abiertos (no pagados ni cancelados), del más antiguo al más nuevo: la cola del servicio.
	// "/activas" es una ruta fija: Spring la prefiere a "/{publicId}", así que no se confunde con un id.
	@GetMapping("/activas")
	public ResponseEntity<ApiResponse<List<OrderResult>>> getActivas(
			@RequestHeader("X-Tenant-Id") UUID tenantId) {

		return ResponseEntity.status(HttpStatus.OK).body(
				ResponseUtil.sucess("Los pedidos activos", getOrderscase.execute(new GetOrdersQuery(tenantId, true))));
	}

}
