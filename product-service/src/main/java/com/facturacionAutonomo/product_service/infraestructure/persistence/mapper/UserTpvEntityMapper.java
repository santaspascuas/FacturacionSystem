package com.facturacionAutonomo.product_service.infraestructure.persistence.mapper;

import com.facturacionAutonomo.product_service.domain.model.Role;
import com.facturacionAutonomo.product_service.domain.model.UsersTpv;
import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.UserTpvEntity;

public class UserTpvEntityMapper {

	// Solo tiene métodos estáticos: no se instancia (igual que los mappers de customer-service y auth-service).
	private UserTpvEntityMapper() {}
	
	public static UserTpvEntity dominioToJpa(UsersTpv usertpv) {
		UserTpvEntity entidadjpa = new UserTpvEntity();
		entidadjpa.setTenantId(usertpv.getTenantId());
		entidadjpa.setUsername(usertpv.getUsername());
		entidadjpa.setRoleTpv(usertpv.getRoletpv().name());
		entidadjpa.setHashPassword(usertpv.getPassword_hash());
		entidadjpa.setCreatedAt(usertpv.getCreatedAt());
		entidadjpa.setUpdatedAt(usertpv.getUpdatedAt());
		return entidadjpa;
	}
	
	
	public static UsersTpv jpaToDominio(UserTpvEntity entidad) {
		Role roltpv = null;
		if(entidad.getRoleTpv() != null) {
			roltpv = Role.valueOf(entidad.getRoleTpv());
		}
		return  UsersTpv.restoreData(entidad.getUsertpvId(), entidad.getTenantId(), entidad.getUsername(), entidad.getHashPassword(), roltpv, entidad.getCreatedAt(), entidad.getUpdatedAt());
		
	}
	
	
	
	

}
