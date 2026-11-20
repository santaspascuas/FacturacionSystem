package com.facturacionAutonomo.documentacionfactura_service.infrastructure.web.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.facturacionAutonomo.documentacionfactura_service.application.port.in.GetCustomerUseCase;
import com.facturacionAutonomo.documentacionfactura_service.application.port.in.GetOrderUseCase;
import com.facturacionAutonomo.documentacionfactura_service.application.query.GetCustomerQuery;
import com.facturacionAutonomo.documentacionfactura_service.application.query.GetOrderQuery;
import com.facturacionAutonomo.documentacionfactura_service.application.result.CustomerResult;
import com.facturacionAutonomo.documentacionfactura_service.application.result.OrderResult;
import com.facturacionAutonomo.documentacionfactura_service.infrastructure.utilitys.ApiResponse;
import com.facturacionAutonomo.documentacionfactura_service.infrastructure.utilitys.ResponseUtil;

/**
 * Endpoints de tickets. De momento, las consultas de lo que necesita un ticket: el pedido de product-service y el
 * emisor (datos fiscales del restaurante) de customer-service. Aquí irá también POST /api/ticket/print.
 */
@RestController
@RequestMapping("/api/ticket")
public class TicketController {

	private final GetOrderUseCase getOrderUseCase;
	private final GetCustomerUseCase getCustomerUseCase;

	public TicketController(GetOrderUseCase getOrderUseCase, GetCustomerUseCase getCustomerUseCase) {
		this.getOrderUseCase = getOrderUseCase;
		this.getCustomerUseCase = getCustomerUseCase;
	}

	// Pedido con sus líneas, sus cobros y si está pagado (si se podrá imprimir el ticket).
	// Sin cabecera o con un UUID mal formado: 400 (GlobalExceptionHandler).
	@GetMapping("/pedido/{orderPublicId}")
	public ResponseEntity<ApiResponse<OrderResult>> getPedido(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@PathVariable UUID orderPublicId) {

		GetOrderQuery query = new GetOrderQuery(tenantId, orderPublicId);

		return ResponseEntity.ok(ResponseUtil.success("Pedido obtenido correctamente", getOrderUseCase.execute(query)));
	}

	// Datos fiscales del restaurante que saldrán en el ticket, y si están completos (si se podrán emitir tickets).
	// Sin cabecera o con un UUID mal formado: 400 (GlobalExceptionHandler).
	@GetMapping("/emisor")
	public ResponseEntity<ApiResponse<CustomerResult>> getEmisor(@RequestHeader("X-Tenant-Id") UUID tenantId) {

		GetCustomerQuery query = new GetCustomerQuery(tenantId);

		return ResponseEntity.ok(ResponseUtil.success("Emisor obtenido correctamente", getCustomerUseCase.execute(query)));
	}
}
