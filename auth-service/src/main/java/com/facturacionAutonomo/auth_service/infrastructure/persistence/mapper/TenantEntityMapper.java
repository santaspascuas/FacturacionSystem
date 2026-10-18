package com.facturacionAutonomo.auth_service.infrastructure.persistence.mapper;

import com.facturacionAutonomo.auth_service.domain.model.Tenant;
import com.facturacionAutonomo.auth_service.infrastructure.persistence.entity.TenantEntity;

public class TenantEntityMapper {
	private TenantEntityMapper() {}

	public static TenantEntity dominiotoJpa(Tenant tenant, TenantEntity destino) {
		destino.setPublicId(tenant.getId());
		destino.setName(tenant.getName());
		destino.setCreatedAt(tenant.getCreatedAt());

		return destino;
	}

	public static Tenant jpaEntityToDominio(TenantEntity entidad) {
		return Tenant.restoreData(entidad.getPublicId(), entidad.getName(), entidad.getCreatedAt());
	}
}
