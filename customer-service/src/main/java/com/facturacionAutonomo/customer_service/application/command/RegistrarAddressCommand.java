package com.facturacionAutonomo.customer_service.application.command;

import java.util.UUID;

public record RegistrarAddressCommand(
		UUID tenantId,
		String calle, 
		String postalCode,
		String city,
		String province,
		String country
		) {

}
