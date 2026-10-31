package com.facturacionAutonomo.customer_service.application.result;

import java.util.UUID;

import com.facturacionAutonomo.customer_service.domain.model.Address;

public record AddressResult(UUID publicId, String calle,String postalCode,
		String city, String province, String country,boolean active ) {

	public static AddressResult from(Address address) {
		return new AddressResult(address.getPublicId(), address.getCalle(), address.getPostalCode(),
				address.getCity(), address.getProvince(), address.getCountry(), address.isActive());
	}
}
