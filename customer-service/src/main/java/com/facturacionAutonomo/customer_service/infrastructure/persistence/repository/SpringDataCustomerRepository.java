package com.facturacionAutonomo.customer_service.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.facturacionAutonomo.customer_service.infrastructure.persistence.entity.CustomerEntity;

public interface SpringDataCustomerRepository extends JpaRepository<CustomerEntity, Long> {

	// LEFT JOIN FETCH: trae customer y direcciones en una sola consulta (sin N+1).
	@Query("SELECT c FROM CustomerEntity c LEFT JOIN FETCH c.addresses WHERE c.tenantId = :tenantId")
	Optional<CustomerEntity> findByTenantId(@Param("tenantId") UUID tenantId);

	boolean existsByTenantId(UUID tenantId);

	// Sin distinguir mayúsculas, igual que el índice único uk_customers_email_lower.
	boolean existsByEmailIgnoreCase(String email);
}
