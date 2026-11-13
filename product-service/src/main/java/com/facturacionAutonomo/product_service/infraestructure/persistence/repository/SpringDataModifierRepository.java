package com.facturacionAutonomo.product_service.infraestructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.ModifierEntity;

public interface SpringDataModifierRepository extends JpaRepository<ModifierEntity, Long>{

	boolean existsByTenantIdAndName(UUID tenantId, String name);

	Optional<ModifierEntity> findByTenantIdAndPublicId(UUID tenantId, UUID publicId);

	List<ModifierEntity> findByTenantIdAndActiveTrueOrderByNameAsc(UUID tenantId);

}
