package com.facturacionAutonomo.auth_service.infrastructure.web.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.facturacionAutonomo.auth_service.infrastructure.web.response.ApiResponse;
import com.facturacionAutonomo.auth_service.infrastructure.web.response.ResponseUtil;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@GetMapping("/me")
	public ResponseEntity<ApiResponse<Map<String, Object>>> me(@AuthenticationPrincipal Jwt jwt) {
		Map<String, Object> datos = new LinkedHashMap<>();
		datos.put("userId", jwt.getSubject());
		datos.put("tenantId", jwt.getClaimAsString("tenant_id"));
		datos.put("roles", jwt.getClaimAsStringList("roles"));
		datos.put("expiresAt", jwt.getExpiresAt());
		return ResponseEntity.ok(ResponseUtil.success("Usuario autenticado", datos));
	}
}
