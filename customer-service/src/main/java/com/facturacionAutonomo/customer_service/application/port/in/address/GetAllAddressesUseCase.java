package com.facturacionAutonomo.customer_service.application.port.in.address;

import java.util.List;

import com.facturacionAutonomo.customer_service.application.query.GetAddressQuery;
import com.facturacionAutonomo.customer_service.application.result.AddressResult;

public interface GetAllAddressesUseCase {

	// Lista plana de direcciones del tenant. Vacía si no tiene ninguna.
	List<AddressResult> execute(GetAddressQuery query);

}
