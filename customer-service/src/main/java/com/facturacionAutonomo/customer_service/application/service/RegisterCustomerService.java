package com.facturacionAutonomo.customer_service.application.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.customer_service.application.command.RegistrarCustomerCommand;
import com.facturacionAutonomo.customer_service.application.port.in.RegisterCustomerUseCase;
import com.facturacionAutonomo.customer_service.application.port.out.CustomerRepositoryPort;
import com.facturacionAutonomo.customer_service.application.result.CustomerResult;
import com.facturacionAutonomo.customer_service.domain.exception.CustomerYaRegistradoException;
import com.facturacionAutonomo.customer_service.domain.exception.DatoInvalidoException;
import com.facturacionAutonomo.customer_service.domain.exception.EmailYaRegistradoException;
import com.facturacionAutonomo.customer_service.domain.model.Customer;

@Service
public class RegisterCustomerService implements RegisterCustomerUseCase {

	private static final Logger log = LoggerFactory.getLogger(RegisterCustomerService.class);

	private final CustomerRepositoryPort customerRepositoryPort;
	private final Clock clock;
	private final EmailUtilInterface emailUtil;
	private final NifUtilInterface nifUtil;
	
	

	public RegisterCustomerService(CustomerRepositoryPort customerRepositoryPort, Clock clock,
			EmailUtilInterface emailUtil, NifUtilInterface nifUtil) {
		this.customerRepositoryPort = customerRepositoryPort;
		this.clock = clock;
		this.emailUtil = emailUtil;
		this.nifUtil = nifUtil;
	}



	@Override
	@Transactional
	public CustomerResult execute(RegistrarCustomerCommand command) {
		if (command.tenantId() == null) {
			throw new DatoInvalidoException("El tenant es obligatorio");
		}
		log.info("[RegisterCustomerService] alta de customer tenantId: {}", command.tenantId());

		if (customerRepositoryPort.existsByTenantId(command.tenantId())) {
			throw new CustomerYaRegistradoException("El tenant ya tiene un customer registrado");
		}
		
		String email = (command.email() == null || command.email().isBlank()) ? null : command.email();
		if (email != null && !emailUtil.esValido(email)) {
			throw new DatoInvalidoException("Email inválido");
		}

		if(!nifUtil.confirmaNif(command.nif())) {
			throw new DatoInvalidoException("Nif invalido");
		}
		// Ya validado (no es null): se guarda siempre en mayúsculas y sin espacios alrededor.
		String nif = command.nif().trim().toUpperCase(Locale.ROOT);

		if (email != null && customerRepositoryPort.existsByEmail(email)) {
			throw new EmailYaRegistradoException("El email ya está registrado");
		}


		Customer customer = Customer.crearCustomer(command.tenantId(), command.razonSocial(), nif, email,
				LocalDateTime.now(clock));

		Customer guardado = customerRepositoryPort.save(customer);
		return CustomerResult.from(guardado);
	}
}
