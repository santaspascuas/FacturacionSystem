package com.facturacionAutonomo.product_service.infraestructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.UserTpvEntity;

public interface SrpingDataUserTpvRepository extends JpaRepository <UserTpvEntity, Long>{
	
	//Listar los usuarios de un tenant con un rol concreto
	@Query("SELECT u FROM UserTpvEntity u WHERE u.tenantId = :tenantId AND u.roleTpv = :role")
	List<UserTpvEntity> listaUserporRole( @Param("tenantId") UUID tenantId, 
	        @Param("role") String role);
	
	//buscar si existe el usuario con el username y el tenantid
	Optional<UserTpvEntity>findByTenantIdAndUsername(UUID tenantId, String username);
	
	boolean existsByTenantIdAndUsername(UUID tenantId, String username);


}
