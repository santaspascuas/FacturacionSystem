package com.facturacionAutonomo.auth_service.application.port.out;

import com.facturacionAutonomo.auth_service.domain.model.Tenant;

public interface TenantRepositoryPort {
	Tenant save(Tenant tenant);
}
