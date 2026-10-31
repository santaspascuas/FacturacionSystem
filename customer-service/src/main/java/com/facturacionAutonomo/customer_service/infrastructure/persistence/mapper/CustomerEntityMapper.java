package com.facturacionAutonomo.customer_service.infrastructure.persistence.mapper;

import java.util.List;

import com.facturacionAutonomo.customer_service.domain.model.Address;
import com.facturacionAutonomo.customer_service.domain.model.Customer;
import com.facturacionAutonomo.customer_service.infrastructure.persistence.entity.CustomerEntity;


public class CustomerEntityMapper {

	private CustomerEntityMapper() {}

	public static Customer jpaToDominio(CustomerEntity entidad) {
		List<Address> addresses = entidad.getAddresses().stream()
				.map(AddressEntityMapper::jpaToDominio)
				.toList();

		return Customer.restoreData(entidad.getTenantId(), entidad.getRazonSocial(), entidad.getNif(),
				entidad.getEmail(), addresses, entidad.getCreatedAt(), entidad.getUpdatedAt());
	}

	/** Alta: entidad nueva con sus direcciones iniciales. */
	public static CustomerEntity dominioToJpa(Customer customer) {

		CustomerEntity usuariojpa = new CustomerEntity();

		usuariojpa.setTenantId(customer.getTenantId());
		usuariojpa.setRazonSocial(customer.getRazonSocial());
		usuariojpa.setNif(customer.getNif());
		usuariojpa.setEmail(customer.getEmail());
		usuariojpa.setCreatedAt(customer.getCreatedAt());
		usuariojpa.setUpdatedAt(customer.getUpdatedAt());

		for (Address address : customer.getAddresses()) {
			usuariojpa.addAddress(AddressEntityMapper.dominioToJpa(address));
		}

		return usuariojpa;
	}

	public static void actualizarJpa(Customer customer, CustomerEntity existente) {
		existente.setRazonSocial(customer.getRazonSocial());
		existente.setNif(customer.getNif());
		existente.setEmail(customer.getEmail());
		existente.setUpdatedAt(customer.getUpdatedAt());
	}
}
