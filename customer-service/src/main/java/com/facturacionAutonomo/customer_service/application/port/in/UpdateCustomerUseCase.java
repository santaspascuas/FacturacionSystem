package com.facturacionAutonomo.customer_service.application.port.in;

import com.facturacionAutonomo.customer_service.application.command.UpdateCustomerCommand;
import com.facturacionAutonomo.customer_service.application.result.CustomerResult;

public interface UpdateCustomerUseCase {
	
	CustomerResult execute(UpdateCustomerCommand update);

}
