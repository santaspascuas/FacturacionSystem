package com.facturacionAutonomo.auth_service.application.port.in;

import com.facturacionAutonomo.auth_service.application.command.RegisterUserCommand;
import com.facturacionAutonomo.auth_service.application.result.RegisterUserResult;

public interface RegisterUserUseCase {
	RegisterUserResult execute(RegisterUserCommand command);
}
