package com.facturacionAutonomo.auth_service.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.facturacionAutonomo.auth_service.infrastructure.persistence.entity.UserEntity;

public interface SpringDataUserRepository extends JpaRepository<UserEntity, Long>{
	boolean existsByEmail(String email);

	@EntityGraph(attributePaths = "tenant")
	Optional<UserEntity> findByPublicId(UUID publicId);

	@EntityGraph(attributePaths = "tenant")
	Optional<UserEntity> findByEmail(String email);
}
