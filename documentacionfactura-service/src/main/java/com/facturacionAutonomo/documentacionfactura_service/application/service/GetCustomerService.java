package com.facturacionAutonomo.documentacionfactura_service.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.facturacionAutonomo.documentacionfactura_service.application.port.in.GetCustomerUseCase;
import com.facturacionAutonomo.documentacionfactura_service.application.port.out.IssuerPort;
import com.facturacionAutonomo.documentacionfactura_service.application.query.GetCustomerQuery;
import com.facturacionAutonomo.documentacionfactura_service.application.result.CustomerResult;
import com.facturacionAutonomo.documentacionfactura_service.domain.exception.DatoInvalidoException;
import com.facturacionAutonomo.documentacionfactura_service.domain.exception.EmisorNoEncontradoException;
import com.facturacionAutonomo.documentacionfactura_service.domain.model.Issuer;

/** Datos fiscales del restaurante (el emisor del ticket). No sabe que detrás está customer-service: usa IssuerPort. */
@Service
public class GetCustomerService implements GetCustomerUseCase {

	private static final Logger log = LoggerFactory.getLogger(GetCustomerService.class);

	private final IssuerPort issuerPort;

	public GetCustomerService(IssuerPort issuerPort) {
		this.issuerPort = issuerPort;
	}

	@Override
	public CustomerResult execute(GetCustomerQuery query) {
		if (query.tenantId() == null) {
			throw new DatoInvalidoException("El tenant es obligatorio");
		}
		log.info("[GetCustomerService] emisor del tenant {}", query.tenantId());

		Issuer emisor = issuerPort.buscarPorTenant(query.tenantId())
				.orElseThrow(() -> new EmisorNoEncontradoException("El restaurante no está registrado"));

		return CustomerResult.from(emisor);
	}
}
