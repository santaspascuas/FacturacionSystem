package com.facturacionAutonomo.auth_service.application.port.out;

import com.facturacionAutonomo.auth_service.application.result.AccessToken;
import com.facturacionAutonomo.auth_service.domain.model.User;

public interface TokenProviderPort {
	AccessToken generarToken(User usuario);
}
