package com.facturacionAutonomo.auth_service.application.port.in;

import com.facturacionAutonomo.auth_service.application.command.LoginCommand;
import com.facturacionAutonomo.auth_service.application.result.AuthTokens;

public interface LoginUserUseCase {
	AuthTokens execute(LoginCommand command);
}
