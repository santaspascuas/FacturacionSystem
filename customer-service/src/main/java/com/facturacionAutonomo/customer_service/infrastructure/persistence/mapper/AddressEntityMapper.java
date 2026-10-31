package com.facturacionAutonomo.customer_service.infrastructure.persistence.mapper;

import com.facturacionAutonomo.customer_service.domain.model.Address;
import com.facturacionAutonomo.customer_service.infrastructure.persistence.entity.AddressEntity;


public class AddressEntityMapper {

	private AddressEntityMapper() {}

	public static Address jpaToDominio(AddressEntity entidad) {
		return Address.restoreData(entidad.getPublicId(), entidad.getStreet(), entidad.getPostalCode(),
				entidad.getCity(), entidad.getProvince(), entidad.getCountry(), entidad.isActive());
	}

	/** Crea la entidad de la dirección. El id y el customer los asigna quien la añade a su CustomerEntity. */
	public static AddressEntity dominioToJpa(Address address) {
		AddressEntity entidad = new AddressEntity();
		entidad.setPublicId(address.getPublicId());
		entidad.setStreet(address.getCalle());
		entidad.setPostalCode(address.getPostalCode());
		entidad.setCity(address.getCity());
		entidad.setProvince(address.getProvince());
		entidad.setCountry(address.getCountry());
		entidad.setActive(address.isActive());
		return entidad;
	}

	/**
	 * Actualización: copia los datos sobre la entidad existente. No toca addressId, publicId ni customer
	 * (no cambian nunca).
	 */
	public static void actualizarJpa(Address address, AddressEntity existente) {
		existente.setStreet(address.getCalle());
		existente.setPostalCode(address.getPostalCode());
		existente.setCity(address.getCity());
		existente.setProvince(address.getProvince());
		existente.setCountry(address.getCountry());
		existente.setActive(address.isActive());
	}
}
