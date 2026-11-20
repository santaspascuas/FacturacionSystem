package com.facturacionAutonomo.documentacionfactura_service.infrastructure.client.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Dirección del restaurante en customer-service (su AddressResult). active = true es la dirección fiscal. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AddressResponse(
		UUID publicId,
		String calle,
		String postalCode,
		String city,
		String province,
		String country,
		boolean active
		) {
}
