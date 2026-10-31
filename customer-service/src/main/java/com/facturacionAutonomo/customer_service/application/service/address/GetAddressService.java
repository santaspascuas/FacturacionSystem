package com.facturacionAutonomo.customer_service.application.service.address;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.customer_service.application.port.in.address.GetAllAddressesUseCase;
import com.facturacionAutonomo.customer_service.application.port.out.AddressRepositoryPort;
import com.facturacionAutonomo.customer_service.application.query.GetAddressQuery;
import com.facturacionAutonomo.customer_service.application.result.AddressResult;

@Service
public class GetAddressService implements GetAllAddressesUseCase{

	private static final Logger log = LoggerFactory.getLogger(GetAddressService.class);

	private final AddressRepositoryPort addressRepository;

	public GetAddressService(AddressRepositoryPort addressRepository) {
		super();
		this.addressRepository = addressRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<AddressResult> execute(GetAddressQuery query) {
		log.info("[GetAddressService] listar direcciones tenantId: {}", query.tenantId());
		return addressRepository.findByTenantId(query.tenantId()).stream().map(AddressResult::from).toList();
	}

}
