package com.facturacionAutonomo.customer_service.application.port.in.address;

import com.facturacionAutonomo.customer_service.application.command.RegistrarAddressCommand;
import com.facturacionAutonomo.customer_service.application.result.AddressResult;

public interface RegisterAddressUseCase {
	
	AddressResult execute(RegistrarAddressCommand command);
}
