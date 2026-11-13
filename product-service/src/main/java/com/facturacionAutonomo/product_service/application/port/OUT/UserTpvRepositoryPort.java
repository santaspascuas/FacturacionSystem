package com.facturacionAutonomo.product_service.application.port.OUT;

import java.util.Optional;
import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.UsersTpv;

public interface UserTpvRepositoryPort {
	
	UsersTpv save(UUID tenantId, UsersTpv usertpv);
	
	boolean existsByTenantIdAndUsername(UUID tenantId, String username);
	
	Optional<UsersTpv> findByByTenantIdAndUsername (UUID tenantId, String username);
	
	
	
	

}
