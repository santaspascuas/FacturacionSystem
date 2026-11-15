package com.facturacionAutonomo.product_service.infraestructure.web.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.facturacionAutonomo.product_service.application.command.payments.RegistrarPaymentCommand;
import com.facturacionAutonomo.product_service.application.port.in.payment.GetPaymentsUseCase;
import com.facturacionAutonomo.product_service.application.port.in.payment.RegistrarPaymentUseCase;
import com.facturacionAutonomo.product_service.application.query.GetPaymentsQuery;
import com.facturacionAutonomo.product_service.application.result.PaymentResult;
import com.facturacionAutonomo.product_service.application.result.RegistrarPaymentResult;
import com.facturacionAutonomo.product_service.infraestructure.utilitys.ApiResponse;
import com.facturacionAutonomo.product_service.infraestructure.utilitys.ResponseUtil;
import com.facturacionAutonomo.product_service.infraestructure.web.dto.RegistrarPaymentRequest;

import jakarta.validation.Valid;

// Los cobros de un pedido. Un cobro siempre pertenece a un pedido, por eso cuelgan de su ruta
// (como los modificadores de un producto). Sin PUT ni DELETE: un cobro es un registro de caja inmutable.
@RestController
@RequestMapping("/api/order/{orderPublicId}/payments")
public class PaymentController {

	private final RegistrarPaymentUseCase registrarPaymentcase;

	private final GetPaymentsUseCase getPaymentscase;

	public PaymentController(RegistrarPaymentUseCase registrarPaymentcase, GetPaymentsUseCase getPaymentscase) {
		super();
		this.registrarPaymentcase = registrarPaymentcase;
		this.getPaymentscase = getPaymentscase;
	}

	// Registrar un cobro. Devuelve el cobro (con el cambio, si es en efectivo) y el pedido actualizado.
	@PostMapping
	public ResponseEntity<ApiResponse<RegistrarPaymentResult>> registrar(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@PathVariable UUID orderPublicId,
			@Valid @RequestBody RegistrarPaymentRequest dto) {

		RegistrarPaymentCommand command = new RegistrarPaymentCommand(tenantId, orderPublicId, dto.getPaymentType(),
				dto.getCantidad(), dto.getIdempotencyKey());
		return ResponseEntity.status(HttpStatus.CREATED).body(
				ResponseUtil.sucess("Cobro registrado correctamente", registrarPaymentcase.execute(command)));
	}

	// Los cobros del pedido, en el orden en que se hicieron.
	@GetMapping
	public ResponseEntity<ApiResponse<List<PaymentResult>>> listar(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@PathVariable UUID orderPublicId) {

		return ResponseEntity.status(HttpStatus.OK).body(
				ResponseUtil.sucess("Los cobros del pedido", getPaymentscase.execute(new GetPaymentsQuery(tenantId, orderPublicId))));
	}

}
