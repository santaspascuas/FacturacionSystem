package com.facturacionAutonomo.auth_service.infrastructure.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.facturacionAutonomo.auth_service.application.command.LoginCommand;
import com.facturacionAutonomo.auth_service.application.command.LogoutCommand;
import com.facturacionAutonomo.auth_service.application.command.RefreshSessionCommand;
import com.facturacionAutonomo.auth_service.application.command.RegisterUserCommand;
import com.facturacionAutonomo.auth_service.application.result.RegisterUserResult;
import com.facturacionAutonomo.auth_service.application.port.in.LoginUserUseCase;
import com.facturacionAutonomo.auth_service.application.port.in.LogoutUseCase;
import com.facturacionAutonomo.auth_service.application.port.in.RefreshSessionUseCase;
import com.facturacionAutonomo.auth_service.application.port.in.RegisterUserUseCase;
import com.facturacionAutonomo.auth_service.infrastructure.web.dto.LoginRequest;
import com.facturacionAutonomo.auth_service.infrastructure.web.dto.LogoutRequest;
import com.facturacionAutonomo.auth_service.infrastructure.web.dto.RefreshRequest;
import com.facturacionAutonomo.auth_service.infrastructure.web.dto.RegisterRequest;
import com.facturacionAutonomo.auth_service.infrastructure.web.dto.TokenResponse;
import com.facturacionAutonomo.auth_service.infrastructure.web.response.ApiResponse;
import com.facturacionAutonomo.auth_service.infrastructure.web.response.ResponseUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private final RegisterUserUseCase registerUsercase;

	private final LoginUserUseCase loginUsercase;

	private final RefreshSessionUseCase refrescosession;

	private final LogoutUseCase logoutUseCase;

	public AuthController(RegisterUserUseCase registerUsercase, LoginUserUseCase loginUsercase,
			RefreshSessionUseCase refrescosession, LogoutUseCase logoutUseCase) {
		this.registerUsercase = registerUsercase;
		this.loginUsercase = loginUsercase;
		this.refrescosession = refrescosession;
		this.logoutUseCase = logoutUseCase;
	}

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<RegisterUserResult>> register(@Valid @RequestBody RegisterRequest request){
		RegisterUserCommand command = new RegisterUserCommand(request.email(), request.password());
		return ResponseEntity.status(HttpStatus.CREATED).body(
				ResponseUtil.success("Registro correcto", registerUsercase.execute(command)));
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request){
		LoginCommand command = new LoginCommand(request.getEmail(), request.getPassword());
		return ResponseEntity.ok(
				ResponseUtil.success("Se ha realizado el loguin", TokenResponse.from(loginUsercase.execute(command))));
	}

	@PostMapping("/refresco")
	public ResponseEntity<ApiResponse<TokenResponse>> refrescoController(@Valid @RequestBody RefreshRequest request){
		RefreshSessionCommand command = new RefreshSessionCommand(request.refreshToken());
		return ResponseEntity.ok(
				ResponseUtil.success("Se ha actualizado correctamente", TokenResponse.from(refrescosession.execute(command))));
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@RequestBody(required = false) LogoutRequest request){
		logoutUseCase.execute(new LogoutCommand(request == null ? null : request.refreshToken()));
		return ResponseEntity.noContent().build();
	}
}
