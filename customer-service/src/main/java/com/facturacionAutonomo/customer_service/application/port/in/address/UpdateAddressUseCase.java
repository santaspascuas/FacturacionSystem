package com.facturacionAutonomo.customer_service.application.port.in.address;

import com.facturacionAutonomo.customer_service.application.command.UpdateAddressCommand;
import com.facturacionAutonomo.customer_service.application.result.AddressResult;

public interface UpdateAddressUseCase {
	
	
	AddressResult execute(UpdateAddressCommand command);
	

}
