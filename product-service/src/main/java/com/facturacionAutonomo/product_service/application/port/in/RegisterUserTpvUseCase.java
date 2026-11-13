package com.facturacionAutonomo.product_service.application.port.in;

import com.facturacionAutonomo.product_service.application.command.RegistrarUserTpvCommand;
import com.facturacionAutonomo.product_service.application.result.UserTpvResult;

public interface RegisterUserTpvUseCase {
	
	UserTpvResult execute(RegistrarUserTpvCommand command);
	
}
