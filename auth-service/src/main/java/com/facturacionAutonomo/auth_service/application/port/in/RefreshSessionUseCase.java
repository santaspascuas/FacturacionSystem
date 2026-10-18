package com.facturacionAutonomo.auth_service.application.port.in;

import com.facturacionAutonomo.auth_service.application.command.RefreshSessionCommand;
import com.facturacionAutonomo.auth_service.application.result.AuthTokens;

public interface RefreshSessionUseCase {
	AuthTokens execute(RefreshSessionCommand command);
}
