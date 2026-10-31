package com.facturacionAutonomo.customer_service.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.facturacionAutonomo.customer_service.infrastructure.persistence.entity.AddressEntity;


public interface SpringDataAddressRepository extends JpaRepository<AddressEntity, Long>{

	Optional<AddressEntity> findByPublicIdAndCustomerTenantId(UUID publicId, UUID tenantId);
	
	long deleteByPublicIdAndCustomerTenantId(UUID publicId, UUID tenantId);
	
	// Ordenadas por alta: sin ORDER BY, Postgres no garantiza el mismo orden entre llamadas.
	List<AddressEntity> findByCustomerTenantIdOrderByAddressIdAsc(UUID tenantId);

	// UPDATE directo en BD (no pasa por las entidades): se ejecuta ya, antes de activar la nueva fiscal,
	// para no chocar con el índice único parcial. flushAutomatically: escribe antes lo pendiente.
	@Modifying(flushAutomatically = true)
	@Query("UPDATE AddressEntity a SET a.active = false WHERE a.active = true "
			+ "AND a.customer.id IN (SELECT c.id FROM CustomerEntity c WHERE c.tenantId = :tenantId)")
	int desactivarFiscal(@Param("tenantId") UUID tenantId);

}
