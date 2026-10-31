package com.facturacionAutonomo.customer_service.application.port.in;


import com.facturacionAutonomo.customer_service.application.command.RegistrarCustomerCommand;
import com.facturacionAutonomo.customer_service.application.result.CustomerResult;

public interface RegisterCustomerUseCase {

	CustomerResult execute(RegistrarCustomerCommand command);
}
