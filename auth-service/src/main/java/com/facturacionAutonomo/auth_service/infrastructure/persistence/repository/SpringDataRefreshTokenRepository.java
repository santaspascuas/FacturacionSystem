package com.facturacionAutonomo.auth_service.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.facturacionAutonomo.auth_service.infrastructure.persistence.entity.RefreshTokenEntity;

public interface SpringDataRefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long>{
	@EntityGraph(attributePaths = "userEntidad")
	Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);

	@Query("select r from RefreshTokenEntity r join fetch r.userEntidad u where u.publicId = :userPublicId")
	Optional<RefreshTokenEntity> findByUserEntidadPublicId(@Param("userPublicId") UUID userPublicId);
}
