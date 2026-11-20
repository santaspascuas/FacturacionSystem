package com.facturacionAutonomo.documentacionfactura_service.infrastructure.client.adapter;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import com.facturacionAutonomo.documentacionfactura_service.application.port.out.IssuerPort;
import com.facturacionAutonomo.documentacionfactura_service.domain.model.Issuer;
import com.facturacionAutonomo.documentacionfactura_service.infrastructure.client.dto.CustomerResponse;
import com.facturacionAutonomo.documentacionfactura_service.infrastructure.client.dto.RemoteApiResponse;
import com.facturacionAutonomo.documentacionfactura_service.infrastructure.client.mapper.IssuerResponseMapper;

/** IssuerPort sobre HTTP: GET /api/customer/infome de customer-service. Los DTO remotos no salen de aquí. */
@Component
public class RestCustomerAdapter implements IssuerPort {

	private static final Logger log = LoggerFactory.getLogger(RestCustomerAdapter.class);

	// customer-service envuelve el customer en { success, message, data, errorCode }: el tipo genérico completo.
	private static final ParameterizedTypeReference<RemoteApiResponse<CustomerResponse>> TIPO_CUSTOMER =
			new ParameterizedTypeReference<>() {};

	private final CustomerServiceCaller customerService;

	public RestCustomerAdapter(CustomerServiceCaller customerService) {
		this.customerService = customerService;
	}

	@Override
	public Optional<Issuer> buscarPorTenant(UUID tenantId) {
		log.debug("[customer-service] Consultando datos fiscales del tenant {}", tenantId);
		return customerService.get(tenantId, TIPO_CUSTOMER, "/api/customer/infome")
				.map(IssuerResponseMapper::toDomain);
	}
}
