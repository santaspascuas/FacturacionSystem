package com.facturacionAutonomo.customer_service.infrastructure.web.controller;

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

import com.facturacionAutonomo.customer_service.application.command.DeleteAddressCommand;
import com.facturacionAutonomo.customer_service.application.command.RegistrarAddressCommand;
import com.facturacionAutonomo.customer_service.application.command.SetFiscalAddressCommand;
import com.facturacionAutonomo.customer_service.application.command.UpdateAddressCommand;
import com.facturacionAutonomo.customer_service.application.port.in.address.DeleteAddressUseCase;
import com.facturacionAutonomo.customer_service.application.port.in.address.GetAllAddressesUseCase;
import com.facturacionAutonomo.customer_service.application.port.in.address.RegisterAddressUseCase;
import com.facturacionAutonomo.customer_service.application.port.in.address.SetFiscalAddressUseCase;
import com.facturacionAutonomo.customer_service.application.port.in.address.UpdateAddressUseCase;
import com.facturacionAutonomo.customer_service.application.query.GetAddressQuery;
import com.facturacionAutonomo.customer_service.application.result.AddressResult;
import com.facturacionAutonomo.customer_service.infrastructure.utilitys.ApiResponse;
import com.facturacionAutonomo.customer_service.infrastructure.utilitys.ResponseUtil;
import com.facturacionAutonomo.customer_service.infrastructure.web.dto.AddressRequestDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/address")
public class AddressController {
	
	private final RegisterAddressUseCase registrarAddresscase;
	private final UpdateAddressUseCase updateAddressUsecase;
	private final DeleteAddressUseCase deleteAddressUsecase;
	private final GetAllAddressesUseCase getAddressUsecase;
	private final SetFiscalAddressUseCase setFiscalAddressUsecase;

	public AddressController(RegisterAddressUseCase registrarAddresscase, UpdateAddressUseCase updateAddressUsecase,
			DeleteAddressUseCase deleteAddressUsecase, GetAllAddressesUseCase getAddressUsecase,
			SetFiscalAddressUseCase setFiscalAddressUsecase) {
		super();
		this.registrarAddresscase = registrarAddresscase;
		this.updateAddressUsecase = updateAddressUsecase;
		this.deleteAddressUsecase = deleteAddressUsecase;
		this.getAddressUsecase = getAddressUsecase;
		this.setFiscalAddressUsecase = setFiscalAddressUsecase;
	}

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<AddressResult>> register(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@Valid @RequestBody AddressRequestDto dto){
		
		RegistrarAddressCommand command = new RegistrarAddressCommand(tenantId,dto.getCalle(),dto.getPostalCode(), dto.getCity(),
				dto.getProvince(),dto.getCountry());
		
		return ResponseEntity.status(HttpStatus.CREATED).body(
				ResponseUtil.success("Se añade la direccion al customer", registrarAddresscase.execute(command)));
	}

	@PutMapping("/{publicId}")
	public ResponseEntity<ApiResponse<AddressResult>> updateAddress(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@PathVariable UUID publicId,
			@Valid @RequestBody AddressRequestDto dto
			){
		UpdateAddressCommand command = new UpdateAddressCommand(
				tenantId, publicId, dto.getCalle(),dto.getPostalCode(),dto.getCity(),dto.getProvince(),dto.getCountry());
		
		return ResponseEntity.ok(
				ResponseUtil.success("Se ha actualizado la informacion",updateAddressUsecase.execute(command)));
	}

	// Sin body: la acción es marcar como fiscal la dirección de la ruta.
	@PutMapping("/{publicId}/fiscal")
	public ResponseEntity<ApiResponse<AddressResult>> setFiscalAddress(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@PathVariable UUID publicId
			){

		SetFiscalAddressCommand command = new SetFiscalAddressCommand(tenantId, publicId);

		return ResponseEntity.ok(
				ResponseUtil.success("Direccion fiscal actualizada", setFiscalAddressUsecase.execute(command)));
	}
	
	
	@DeleteMapping("/{publicId}")
	public ResponseEntity<ApiResponse<Void>> deleteAddress(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@PathVariable UUID publicId
			){

		DeleteAddressCommand command = new DeleteAddressCommand(tenantId, publicId);
		deleteAddressUsecase.execute(command);

		return ResponseEntity.ok(ResponseUtil.success("Se ha eliminado la direccion", null));
	}
	
	@GetMapping()
	public ResponseEntity<ApiResponse<List<AddressResult>>> getAllAddress(
			@RequestHeader("X-Tenant-Id") UUID tenantId
			){

		GetAddressQuery query = new GetAddressQuery(tenantId);

		return ResponseEntity.ok(ResponseUtil.success("La lista de las direcciones", getAddressUsecase.execute(query)));
	}

}
