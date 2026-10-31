package com.facturacionAutonomo.customer_service.application.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.facturacionAutonomo.customer_service.domain.model.Address;

public interface AddressRepositoryPort {


	Address save(UUID tenantId, Address address);
	Optional<Address> findByPublicIdAndTenantId(UUID publicId, UUID tenantId);
	/** Borra la dirección si es del tenant. Devuelve false si no existía (o es de otro tenant). */
	boolean deleteByPublicIdAndTenantId(UUID publicId, UUID tenantId);
	/** Direcciones del tenant en orden de alta. Vacía si no tiene. */
	List<Address> findByTenantId(UUID tenantId);
	/** Quita la marca de fiscal a la dirección que la tenga. Llamar antes de activar otra. */
	void desactivarFiscal(UUID tenantId);

}
