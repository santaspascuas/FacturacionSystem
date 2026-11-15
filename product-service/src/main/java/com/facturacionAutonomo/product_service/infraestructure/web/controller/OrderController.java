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
import org.springframework.web.bind.annotation.RestController;

import com.facturacionAutonomo.product_service.application.command.orders.AnadirLineasOrderCommand;
import com.facturacionAutonomo.product_service.application.command.orders.CambiarEstadoOrderCommand;
import com.facturacionAutonomo.product_service.application.command.orders.EliminarLineaOrderCommand;
import com.facturacionAutonomo.product_service.application.command.orders.LineaOrderCommand;
import com.facturacionAutonomo.product_service.application.command.orders.RegistrarOrderCommand;
import com.facturacionAutonomo.product_service.application.command.orders.UpdateLineaOrderCommand;
import com.facturacionAutonomo.product_service.application.command.orders.UpdateOrderCommand;
import com.facturacionAutonomo.product_service.application.port.in.order.AnadirLineasOrderUseCase;
import com.facturacionAutonomo.product_service.application.port.in.order.CambiarEstadoOrderUseCase;
import com.facturacionAutonomo.product_service.application.port.in.order.EliminarLineaOrderUseCase;
import com.facturacionAutonomo.product_service.application.port.in.order.GetOrderUseCase;
import com.facturacionAutonomo.product_service.application.port.in.order.GetOrdersUseCase;
import com.facturacionAutonomo.product_service.application.port.in.order.RegistrarOrderUseCase;
import com.facturacionAutonomo.product_service.application.port.in.order.UpdateLineaOrderUseCase;
import com.facturacionAutonomo.product_service.application.port.in.order.UpdateOrderUseCase;
import com.facturacionAutonomo.product_service.application.query.GetOrderQuery;
import com.facturacionAutonomo.product_service.application.query.GetOrdersQuery;
import com.facturacionAutonomo.product_service.application.result.OrderResult;
import com.facturacionAutonomo.product_service.infraestructure.utilitys.ApiResponse;
import com.facturacionAutonomo.product_service.infraestructure.utilitys.ResponseUtil;
import com.facturacionAutonomo.product_service.infraestructure.web.dto.AnadirLineasOrderRequest;
import com.facturacionAutonomo.product_service.infraestructure.web.dto.CambiarEstadoOrderRequest;
import com.facturacionAutonomo.product_service.infraestructure.web.dto.LineaOrderRequest;
import com.facturacionAutonomo.product_service.infraestructure.web.dto.RegistrarOrderRequest;
import com.facturacionAutonomo.product_service.infraestructure.web.dto.UpdateLineaOrderRequest;
import com.facturacionAutonomo.product_service.infraestructure.web.dto.UpdateOrderRequest;

import jakarta.validation.Valid;

// Un pedido no se borra nunca (es historial de facturación); se anula con el estado CANCELADO.
// Lo que sí se borra son sus líneas, mientras el pedido está abierto.
// Todas las modificaciones devuelven el pedido entero con los totales recalculados, para que el TPV pinte la cuenta.
@RestController
@RequestMapping("/api/order")
public class OrderController {

	private final GetOrdersUseCase getOrderscase;

	private final GetOrderUseCase getOrdercase;

	private final RegistrarOrderUseCase registrarOrdercase;

	private final CambiarEstadoOrderUseCase cambiarEstadoOrdercase;

	private final AnadirLineasOrderUseCase anadirLineasOrdercase;

	private final UpdateLineaOrderUseCase updateLineaOrdercase;

	private final EliminarLineaOrderUseCase eliminarLineaOrdercase;

	private final UpdateOrderUseCase updateOrdercase;

	public OrderController(GetOrdersUseCase getOrderscase, GetOrderUseCase getOrdercase,
			RegistrarOrderUseCase registrarOrdercase, CambiarEstadoOrderUseCase cambiarEstadoOrdercase,
			AnadirLineasOrderUseCase anadirLineasOrdercase, UpdateLineaOrderUseCase updateLineaOrdercase,
			EliminarLineaOrderUseCase eliminarLineaOrdercase, UpdateOrderUseCase updateOrdercase) {
		super();
		this.getOrderscase = getOrderscase;
		this.getOrdercase = getOrdercase;
		this.registrarOrdercase = registrarOrdercase;
		this.cambiarEstadoOrdercase = cambiarEstadoOrdercase;
		this.anadirLineasOrdercase = anadirLineasOrdercase;
		this.updateLineaOrdercase = updateLineaOrdercase;
		this.eliminarLineaOrdercase = eliminarLineaOrdercase;
		this.updateOrdercase = updateOrdercase;
	}

	// 1. Todos los pedidos del restaurante, del más reciente al más antiguo.
	@GetMapping("/all")
	public ResponseEntity<ApiResponse<List<OrderResult>>> getAll(
			@RequestHeader("X-Tenant-Id") UUID tenantId) {

		return ResponseEntity.status(HttpStatus.OK).body(
				ResponseUtil.sucess("La lista de los pedidos", getOrderscase.execute(new GetOrdersQuery(tenantId, false))));
	}

	// 2. Pedidos abiertos (no pagados ni cancelados), del más antiguo al más nuevo: la cola del servicio.
	// "/activas" es una ruta fija: Spring la prefiere a "/{publicId}", así que no se confunde con un id.
	@GetMapping("/activas")
	public ResponseEntity<ApiResponse<List<OrderResult>>> getActivas(
			@RequestHeader("X-Tenant-Id") UUID tenantId) {

		return ResponseEntity.status(HttpStatus.OK).body(
				ResponseUtil.sucess("Los pedidos activos", getOrderscase.execute(new GetOrdersQuery(tenantId, true))));
	}

	// 3. Un pedido con sus líneas.
	@GetMapping("/{publicId}")
	public ResponseEntity<ApiResponse<OrderResult>> getOne(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@PathVariable UUID publicId) {

		return ResponseEntity.status(HttpStatus.OK).body(
				ResponseUtil.sucess("El pedido", getOrdercase.execute(new GetOrderQuery(tenantId, publicId))));
	}

	// 4. Crear un pedido con sus líneas.
	@PostMapping("/register")
	public ResponseEntity<ApiResponse<OrderResult>> register(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@Valid @RequestBody RegistrarOrderRequest dto) {

		RegistrarOrderCommand command = new RegistrarOrderCommand(tenantId, dto.getOrderType(), dto.getTablePublicId(),
				dto.getNumPersonas(), dto.getIdempotencyKey(), dto.getClientTimestamp(), aComandos(dto.getLineas()));
		return ResponseEntity.status(HttpStatus.CREATED).body(
				ResponseUtil.sucess("Pedido registrado correctamente", registrarOrdercase.execute(command)));
	}

	// 5. Cambiar los datos del pedido: hoy solo el número de personas. 400 si el pedido está cerrado.
	@PutMapping("/{publicId}")
	public ResponseEntity<ApiResponse<OrderResult>> update(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@PathVariable UUID publicId,
			@Valid @RequestBody UpdateOrderRequest dto) {

		UpdateOrderCommand command = new UpdateOrderCommand(tenantId, publicId, dto.getNumPersonas());
		return ResponseEntity.status(HttpStatus.OK).body(
				ResponseUtil.sucess("Pedido actualizado", updateOrdercase.execute(command)));
	}

	// 6. Cambiar el estado: avanzar en cocina o anular (CANCELADO, solo si no hay nada cobrado).
	// CIERRAYPAGA no se acepta (400): el pedido se cierra solo al cobrarlo entero.
	@PatchMapping("/{publicId}/status")
	public ResponseEntity<ApiResponse<OrderResult>> cambiarEstado(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@PathVariable UUID publicId,
			@Valid @RequestBody CambiarEstadoOrderRequest dto) {

		CambiarEstadoOrderCommand command = new CambiarEstadoOrderCommand(tenantId, publicId, dto.getStatus());
		return ResponseEntity.status(HttpStatus.OK).body(
				ResponseUtil.sucess("Estado del pedido actualizado", cambiarEstadoOrdercase.execute(command)));
	}

	// 7. Añadir platos a un pedido abierto (otra ronda, el postre...). El pedido vuelve a ENVIADO para que cocina
	// los vea. Con la misma idempotencyKey, un reintento devuelve el pedido sin duplicar las líneas.
	// 404 si el pedido o algún producto no existe; 400 si el pedido ya está pagado o cancelado.
	@PostMapping("/{publicId}/items")
	public ResponseEntity<ApiResponse<OrderResult>> anadirLineas(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@PathVariable UUID publicId,
			@Valid @RequestBody AnadirLineasOrderRequest dto) {

		AnadirLineasOrderCommand command = new AnadirLineasOrderCommand(tenantId, publicId, dto.getIdempotencyKey(),
				aComandos(dto.getLineas()));
		return ResponseEntity.status(HttpStatus.OK).body(
				ResponseUtil.sucess("Platos añadidos al pedido", anadirLineasOrdercase.execute(command)));
	}

	// 8. Cambiar la cantidad y las notas de una línea. Si la cantidad sube, el pedido vuelve a ENVIADO.
	@PutMapping("/{publicId}/items/{itemPublicId}")
	public ResponseEntity<ApiResponse<OrderResult>> updateLinea(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@PathVariable UUID publicId,
			@PathVariable UUID itemPublicId,
			@Valid @RequestBody UpdateLineaOrderRequest dto) {

		UpdateLineaOrderCommand command = new UpdateLineaOrderCommand(tenantId, publicId, itemPublicId,
				dto.getQuantity(), dto.getNotes());
		return ResponseEntity.status(HttpStatus.OK).body(
				ResponseUtil.sucess("Línea del pedido actualizada", updateLineaOrdercase.execute(command)));
	}

	// 9. Quitar un plato. No se puede quitar el último: para anular el pedido entero está CANCELADO.
	@DeleteMapping("/{publicId}/items/{itemPublicId}")
	public ResponseEntity<ApiResponse<OrderResult>> eliminarLinea(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@PathVariable UUID publicId,
			@PathVariable UUID itemPublicId) {

		EliminarLineaOrderCommand command = new EliminarLineaOrderCommand(tenantId, publicId, itemPublicId);
		return ResponseEntity.status(HttpStatus.OK).body(
				ResponseUtil.sucess("Línea quitada del pedido", eliminarLineaOrdercase.execute(command)));
	}

	// Las líneas llegan igual en el alta y al añadir platos: una sola conversión DTO -> command.
	private List<LineaOrderCommand> aComandos(List<LineaOrderRequest> lineas) {
		return lineas.stream()
				.map(linea -> new LineaOrderCommand(linea.getMenuItemPublicId(), linea.getQuantity(), linea.getNotes()))
				.toList();
	}

}
