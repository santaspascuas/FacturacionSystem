package com.facturacionAutonomo.customer_service.application.port.in.address;

import com.facturacionAutonomo.customer_service.application.command.DeleteAddressCommand;

public interface DeleteAddressUseCase {
	
	//Borrado de la direccion
	
	void execute(DeleteAddressCommand command);

}
