package com.facturacionAutonomo.auth_service.application.port.in;

import com.facturacionAutonomo.auth_service.application.command.LogoutCommand;

public interface LogoutUseCase {
	void execute(LogoutCommand command);
}
