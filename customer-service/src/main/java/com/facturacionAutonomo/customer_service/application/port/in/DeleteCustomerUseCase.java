package com.facturacionAutonomo.customer_service.application.port.in;

import com.facturacionAutonomo.customer_service.application.command.DeleteCustomerCommand;

public interface DeleteCustomerUseCase {

	/** Borra los datos fiscales del tenant (customer y direcciones). No devuelve nada: el controller responde 204. */
	void execute(DeleteCustomerCommand command);

}
