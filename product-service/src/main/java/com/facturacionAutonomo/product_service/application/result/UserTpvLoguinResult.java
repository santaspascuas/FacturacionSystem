package com.facturacionAutonomo.product_service.application.result;

import com.facturacionAutonomo.product_service.domain.model.Role;
import com.facturacionAutonomo.product_service.domain.model.UsersTpv;

public record UserTpvLoguinResult(Role roletpv, String username, boolean confirmaAcceso) {
	
	public static UserTpvLoguinResult convertirA(UsersTpv usuario) {
		return new UserTpvLoguinResult(usuario.getRoletpv(),usuario.getUsername(), true);	
	}

}
