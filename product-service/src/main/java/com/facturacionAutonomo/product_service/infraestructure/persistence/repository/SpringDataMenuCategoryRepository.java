package com.facturacionAutonomo.product_service.infraestructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.MenuCategoryEntity;

public interface SpringDataMenuCategoryRepository extends JpaRepository<MenuCategoryEntity, Long>{

	boolean existsByTenantIdAndName(UUID tenantId, String name);


	List<MenuCategoryEntity> findByTenantIdAndActiveTrueOrderByDisplayOrderAscCategoryIdAsc(UUID tenantId);
	
	@Query("SELECT COALESCE(MAX(c.displayOrder), 0) FROM MenuCategoryEntity c "
			+ "WHERE c.tenantId = :tenantId")
	int findMaxDisplayOrderByTenantId(@Param("tenantId") UUID tenantId);
	
	Optional<MenuCategoryEntity>findByTenantIdAndPublicId(UUID tenantId, UUID publicId);

}
