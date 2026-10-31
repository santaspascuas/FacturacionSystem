package com.facturacionAutonomo.customer_service.application.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.customer_service.application.command.UpdateCustomerCommand;
import com.facturacionAutonomo.customer_service.application.port.in.UpdateCustomerUseCase;
import com.facturacionAutonomo.customer_service.application.port.out.CustomerRepositoryPort;
import com.facturacionAutonomo.customer_service.application.result.CustomerResult;
import com.facturacionAutonomo.customer_service.domain.exception.CustomerNoEncontradoException;
import com.facturacionAutonomo.customer_service.domain.exception.DatoInvalidoException;
import com.facturacionAutonomo.customer_service.domain.model.Customer;

@Service
public class UpdateCustomerService implements UpdateCustomerUseCase{


	private final CustomerRepositoryPort customerRepositoryPort;
	private final Clock clock;
	private final NifUtilInterface nifUtil;

	private static final Logger log = LoggerFactory.getLogger(UpdateCustomerService.class);




	public UpdateCustomerService(CustomerRepositoryPort customerRepositoryPort, Clock clock,
			NifUtilInterface nifUtil) {
		super();
		this.customerRepositoryPort = customerRepositoryPort;
		this.clock = clock;
		this.nifUtil = nifUtil;
	}




	@Override
	@Transactional
	public CustomerResult execute(UpdateCustomerCommand command) {

		if (command.tenantId() == null) {
			throw new DatoInvalidoException("El tenant es obligatorio");
		}

		log.info("[UpdateCustomerService] actualizacion de customer tenantId: {}", command.tenantId());

		//Como es un update. Nos traemos el customer actual: si no existe, 404.
		Customer baseCustomer = customerRepositoryPort.findByTenantId(command.tenantId())
				.orElseThrow(() -> new CustomerNoEncontradoException("El tenant no tiene un customer registrado"));

		if (!nifUtil.confirmaNif(command.nif())) {
			throw new DatoInvalidoException("Nif invalido");
		}
		// Ya validado (no es null): se guarda siempre en mayúsculas y sin espacios alrededor, como en el alta.
		String nif = command.nif().trim().toUpperCase(Locale.ROOT);

		// Solo cambian razón social, NIF y updatedAt; el email no se modifica aquí.
		Customer actualizado = baseCustomer.actualizarDatos(command.razonSocial(), nif, LocalDateTime.now(clock));

		Customer guardado = customerRepositoryPort.save(actualizado);
		return CustomerResult.from(guardado);
	}

}
