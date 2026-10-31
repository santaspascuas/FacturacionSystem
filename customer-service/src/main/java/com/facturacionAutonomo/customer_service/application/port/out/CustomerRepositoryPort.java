package com.facturacionAutonomo.customer_service.application.port.out;

import java.util.Optional;
import java.util.UUID;

import com.facturacionAutonomo.customer_service.domain.model.Customer;

public interface CustomerRepositoryPort {

	Customer save(Customer customer);

	Optional<Customer> findByTenantId(UUID tenantId);

	boolean existsByTenantId(UUID tenantId);

	boolean existsByEmail(String email);

	/** Borra el customer del tenant y sus direcciones. Si no existe, no hace nada. */
	void deleteByTenantId(UUID tenantId);
}
