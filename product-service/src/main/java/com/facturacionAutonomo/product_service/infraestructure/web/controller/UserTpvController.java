package com.facturacionAutonomo.product_service.infraestructure.web.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.facturacionAutonomo.product_service.application.command.LoguinUserTpvCommand;
import com.facturacionAutonomo.product_service.application.command.RegistrarUserTpvCommand;
import com.facturacionAutonomo.product_service.application.port.in.LoguinUserTpvUseCase;
import com.facturacionAutonomo.product_service.application.port.in.RegisterUserTpvUseCase;
import com.facturacionAutonomo.product_service.application.result.UserTpvLoguinResult;
import com.facturacionAutonomo.product_service.application.result.UserTpvResult;
import com.facturacionAutonomo.product_service.infraestructure.utilitys.ApiResponse;
import com.facturacionAutonomo.product_service.infraestructure.utilitys.ResponseUtil;
import com.facturacionAutonomo.product_service.infraestructure.web.dto.LoguinUserTpvRequest;
import com.facturacionAutonomo.product_service.infraestructure.web.dto.UsertpvRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usertpv")
public class UserTpvController {
	
	private final RegisterUserTpvUseCase registerUsertpvcase;
	private final LoguinUserTpvUseCase loguinUsertpvcase;

	public UserTpvController(RegisterUserTpvUseCase registerUsertpvcase, LoguinUserTpvUseCase loguinUsertpvcase) {
		super();
		this.registerUsertpvcase = registerUsertpvcase;
		this.loguinUsertpvcase = loguinUsertpvcase;
	}
	
	
	@PostMapping("/register")
	public ResponseEntity<ApiResponse<UserTpvResult>> register(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			 @RequestBody UsertpvRequest dto
			){
		
		RegistrarUserTpvCommand command = new RegistrarUserTpvCommand(tenantId,dto.getUsername(), dto.getPassword());
		return ResponseEntity.status(HttpStatus.CREATED).body(
				ResponseUtil.sucess("Se registra el usuario de tpv", registerUsertpvcase.execute(command))
				);
	}

	// Comprueba el username y la contraseña de un usuario del TPV dentro del tenant de la cabecera.
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<UserTpvLoguinResult>> loguin(
			@RequestHeader("X-Tenant-Id") UUID tenantId,
			@Valid @RequestBody LoguinUserTpvRequest dto) {

		LoguinUserTpvCommand command = new LoguinUserTpvCommand(tenantId, dto.getUsername(), dto.getPassword());
		return ResponseEntity.ok(
				ResponseUtil.sucess("Usuario de tpv identificado", loguinUsertpvcase.execute(command)));
	}
			
		

	

}
