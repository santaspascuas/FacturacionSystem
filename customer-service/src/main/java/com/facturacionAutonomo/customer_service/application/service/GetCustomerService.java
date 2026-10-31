package com.facturacionAutonomo.customer_service.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.facturacionAutonomo.customer_service.application.port.in.GetCustomerUseCase;
import com.facturacionAutonomo.customer_service.application.port.out.CustomerRepositoryPort;
import com.facturacionAutonomo.customer_service.application.query.GetCustomerQuery;
import com.facturacionAutonomo.customer_service.application.result.CustomerResult;
import com.facturacionAutonomo.customer_service.domain.exception.CustomerNoEncontradoException;
import com.facturacionAutonomo.customer_service.domain.exception.DatoInvalidoException;
import com.facturacionAutonomo.customer_service.domain.model.Customer;

@Service
public class GetCustomerService implements GetCustomerUseCase{

	private static final Logger log = LoggerFactory.getLogger(GetCustomerService.class);

	private final CustomerRepositoryPort customerRepositoryPort;

	public GetCustomerService(CustomerRepositoryPort customerRepositoryPort) {
		super();
		this.customerRepositoryPort = customerRepositoryPort;
	}
	
	
	@Override
	public CustomerResult execute(GetCustomerQuery query) {
		if (query.tenantId() == null) {
			throw new DatoInvalidoException("El tenant es obligatorio");
		}
		log.info("[CustomerResult execute---get: {}]", query.tenantId());

		Customer customer = customerRepositoryPort.findByTenantId(query.tenantId())
				.orElseThrow(() -> new CustomerNoEncontradoException("El tenant no tiene un customer registrado"));

		return CustomerResult.from(customer);
	}

}
