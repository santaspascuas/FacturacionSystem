package com.facturacionAutonomo.auth_service.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.facturacionAutonomo.auth_service.infrastructure.persistence.entity.TenantEntity;

public interface SpringDataTenantRepository extends JpaRepository<TenantEntity, Long> {
	Optional<TenantEntity> findByPublicId(UUID publicId);
}
