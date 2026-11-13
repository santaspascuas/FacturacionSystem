package com.facturacionAutonomo.product_service.application.result;

import java.time.LocalDateTime;
import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.Role;
import com.facturacionAutonomo.product_service.domain.model.UsersTpv;

public record UserTpvResult(UUID tenant, String username,Role roletpv,LocalDateTime createdAt) {
	
	//Como un mapper pero con records
	
	public static UserTpvResult convertirA(UsersTpv usertpv) {
		
		return new UserTpvResult(usertpv.getTenantId(),usertpv.getUsername(),
				usertpv.getRoletpv(),usertpv.getCreatedAt());  	
		
	}

}
