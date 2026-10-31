package com.facturacionAutonomo.customer_service.application.port.in.address;

import com.facturacionAutonomo.customer_service.application.command.SetFiscalAddressCommand;
import com.facturacionAutonomo.customer_service.application.result.AddressResult;

public interface SetFiscalAddressUseCase {

	// Marca la dirección como fiscal y desmarca la anterior.
	AddressResult execute(SetFiscalAddressCommand command);

}
