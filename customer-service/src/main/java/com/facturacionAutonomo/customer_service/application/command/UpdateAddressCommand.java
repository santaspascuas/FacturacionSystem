package com.facturacionAutonomo.customer_service.application.command;

import java.util.UUID;

public record UpdateAddressCommand(
		UUID tenantId,
		UUID publicId,
		String calle, 
		String postalCode,
		String city,
		String province,
		String country
		) {

}
