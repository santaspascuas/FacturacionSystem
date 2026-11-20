package com.facturacionAutonomo.customer_service.application.result;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.facturacionAutonomo.customer_service.domain.model.Customer;

/**
 * Datos del customer que devuelven los casos de uso (registro y consulta), con todas sus direcciones.
 * Incluye el NIF: documentacionfactura-service lo necesita para imprimir los tickets (dato fiscal obligatorio).
 */
public record CustomerResult(UUID tenant, String razonSocial, String nif,
		LocalDateTime createdAt,List<AddressResult> addresses ) {

	/** Conversión única para todos los casos de uso: así el registro y la consulta devuelven lo mismo. */
	public static CustomerResult from(Customer customer) {
		List<AddressResult> addresses = customer.getAddresses().stream()
				.map(AddressResult::from)
				.toList();
		return new CustomerResult(customer.getTenantId(), customer.getRazonSocial(), customer.getNif(),
				customer.getCreatedAt(), addresses);
	}
}
