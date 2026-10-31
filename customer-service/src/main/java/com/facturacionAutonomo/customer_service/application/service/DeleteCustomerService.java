package com.facturacionAutonomo.customer_service.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.customer_service.application.command.DeleteCustomerCommand;
import com.facturacionAutonomo.customer_service.application.port.in.DeleteCustomerUseCase;
import com.facturacionAutonomo.customer_service.application.port.out.CustomerRepositoryPort;
import com.facturacionAutonomo.customer_service.domain.exception.CustomerNoEncontradoException;
import com.facturacionAutonomo.customer_service.domain.exception.DatoInvalidoException;

@Service
public class DeleteCustomerService implements DeleteCustomerUseCase{

	private static final Logger log = LoggerFactory.getLogger(DeleteCustomerService.class);

	private final CustomerRepositoryPort customerRepositoryPort;

	public DeleteCustomerService(CustomerRepositoryPort customerRepositoryPort) {
		super();
		this.customerRepositoryPort = customerRepositoryPort;
	}

	
	@Override
	@Transactional
	public void execute(DeleteCustomerCommand command) {
		if (command.tenantId() == null) {
			throw new DatoInvalidoException("El tenant es obligatorio");
		}
		log.info("[DeleteCustomerService] borrado de customer tenantId: {}", command.tenantId());

		if (!customerRepositoryPort.existsByTenantId(command.tenantId())) {
			throw new CustomerNoEncontradoException("El tenant no tiene un customer registrado");
		}

		customerRepositoryPort.deleteByTenantId(command.tenantId());
	}

}
