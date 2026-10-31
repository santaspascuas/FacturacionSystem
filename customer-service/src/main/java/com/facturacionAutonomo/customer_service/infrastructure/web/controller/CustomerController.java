package com.facturacionAutonomo.customer_service.infrastructure.web.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.facturacionAutonomo.customer_service.application.command.DeleteCustomerCommand;
import com.facturacionAutonomo.customer_service.application.command.RegistrarCustomerCommand;
import com.facturacionAutonomo.customer_service.application.command.UpdateCustomerCommand;
import com.facturacionAutonomo.customer_service.application.port.in.DeleteCustomerUseCase;
import com.facturacionAutonomo.customer_service.application.port.in.GetCustomerUseCase;
import com.facturacionAutonomo.customer_service.application.port.in.RegisterCustomerUseCase;
import com.facturacionAutonomo.customer_service.application.port.in.UpdateCustomerUseCase;
import com.facturacionAutonomo.customer_service.application.query.GetCustomerQuery;
import com.facturacionAutonomo.customer_service.application.result.CustomerResult;
import com.facturacionAutonomo.customer_service.infrastructure.utilitys.ApiResponse;
import com.facturacionAutonomo.customer_service.infrastructure.utilitys.ResponseUtil;
import com.facturacionAutonomo.customer_service.infrastructure.web.dto.CustomerResponseDto;
import com.facturacionAutonomo.customer_service.infrastructure.web.dto.CustomerUpdateDto;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/customer")
public class CustomerController {

	private final RegisterCustomerUseCase registerCustomercase;
	private final GetCustomerUseCase getCustomercase;
	private final UpdateCustomerUseCase updateCustomercase;
	private final DeleteCustomerUseCase deleteCustomercase;
	
	
	

	public CustomerController(RegisterCustomerUseCase registerCustomercase, GetCustomerUseCase getCustomercase,
			UpdateCustomerUseCase updateCustomercase, DeleteCustomerUseCase deleteCustomercase) {
		super();
		this.registerCustomercase = registerCustomercase;
		this.getCustomercase = getCustomercase;
		this.updateCustomercase = updateCustomercase;
		this.deleteCustomercase = deleteCustomercase;
	}

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<CustomerResult>> register(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@Valid @RequestBody CustomerResponseDto dto) {

		RegistrarCustomerCommand command = new RegistrarCustomerCommand(tenantId, dto.getRazonSocial(),
				dto.getNif(), dto.getEmail());

		return ResponseEntity.status(HttpStatus.CREATED).body(
				ResponseUtil.success("Customer registrado correctamente", registerCustomercase.execute(command)));
	}

	@GetMapping("/infome")
	public ResponseEntity<ApiResponse<CustomerResult>> getMe(@RequestHeader("X-Tenant-Id") UUID tenantId) {

		GetCustomerQuery query = new GetCustomerQuery(tenantId);

		return ResponseEntity.ok(
				ResponseUtil.success("Customer obtenido correctamente", getCustomercase.execute(query)));
	}
	
	
	@PutMapping("/actualizaInfo")
	public ResponseEntity<ApiResponse<CustomerResult>>  updateCustomer(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@Valid @RequestBody CustomerUpdateDto updatedto
			){
		
		UpdateCustomerCommand command = new UpdateCustomerCommand(tenantId, updatedto.getRazonSocial(),updatedto.getNif());
		
		return ResponseEntity.ok(
				ResponseUtil.success("Actualizamos al customer",updateCustomercase.execute(command)));
	}
	
	
	@DeleteMapping("/delete")
	public ResponseEntity<ApiResponse<Void>> deleteCustomer(
			@RequestHeader("X-Tenant-Id") UUID tenantId){

		DeleteCustomerCommand command = new DeleteCustomerCommand(tenantId);
		deleteCustomercase.execute(command);

		return ResponseEntity.ok(ResponseUtil.success("Customer eliminado correctamente", null));
	}
	
	
}
