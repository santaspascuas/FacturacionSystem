package com.facturacionAutonomo.product_service.application.port.in;

import com.facturacionAutonomo.product_service.application.command.LoguinUserTpvCommand;
import com.facturacionAutonomo.product_service.application.result.UserTpvLoguinResult;

public interface LoguinUserTpvUseCase {
	
	UserTpvLoguinResult  execute(LoguinUserTpvCommand command);
	

}
