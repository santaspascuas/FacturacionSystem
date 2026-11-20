package com.facturacionAutonomo.documentacionfactura_service.infrastructure.client.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Datos del restaurante tal como los devuelve customer-service en GET /api/customer/infome (su CustomerResult).
 * Los nombres de los campos tienen que coincidir con las claves del JSON: addresses es una lista con todas las
 * direcciones; la fiscal es la que tiene active = true (la elige IssuerResponseMapper).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CustomerResponse(
		UUID tenant,
		String razonSocial,
		String nif,
		LocalDateTime createdAt,
		List<AddressResponse> addresses
		) {
}
