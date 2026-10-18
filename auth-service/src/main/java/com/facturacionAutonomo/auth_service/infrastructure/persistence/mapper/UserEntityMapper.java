package com.facturacionAutonomo.auth_service.infrastructure.persistence.mapper;

import com.facturacionAutonomo.auth_service.domain.model.Email;
import com.facturacionAutonomo.auth_service.domain.model.User;
import com.facturacionAutonomo.auth_service.infrastructure.persistence.entity.TenantEntity;
import com.facturacionAutonomo.auth_service.infrastructure.persistence.entity.UserEntity;

public class UserEntityMapper {
	private UserEntityMapper() {}

	public static UserEntity cambioEntityJpa(User usuario, TenantEntity tenant, UserEntity destino) {
		destino.setPublicId(usuario.getId());
		destino.setTenant(tenant);
		destino.setEmail(usuario.getEmail().value());
		destino.setPassword(usuario.getPasswordHash());
		destino.setRole(usuario.getRole());
		destino.setCreatedAt(usuario.getCreatedAt());
		destino.setAccountNonExpired(usuario.isAccountNonExpired());
		destino.setAccountNonLocked(usuario.isAccountNonLocked());
		destino.setFailedAttempts(usuario.getFailedAttempts());
		destino.setLockTime(usuario.getLockTime());
		return destino;
	}

	public static User cambioEntidadDominio(UserEntity entidad) {
		return User.restoreDataUser(entidad.getPublicId(),
				entidad.getTenant().getPublicId(), new Email(entidad.getEmail()), entidad.getPassword(),
				entidad.getRole(), entidad.getCreatedAt(),
				entidad.isAccountNonLocked(), entidad.isAccountNonExpired(),
				entidad.getFailedAttempts(), entidad.getLockTime());
	}
}
