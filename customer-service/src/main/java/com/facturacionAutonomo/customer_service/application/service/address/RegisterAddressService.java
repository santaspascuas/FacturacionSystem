package com.facturacionAutonomo.customer_service.application.service.address;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.customer_service.application.command.RegistrarAddressCommand;
import com.facturacionAutonomo.customer_service.application.port.in.address.RegisterAddressUseCase;
import com.facturacionAutonomo.customer_service.application.port.out.AddressRepositoryPort;
import com.facturacionAutonomo.customer_service.application.result.AddressResult;
import com.facturacionAutonomo.customer_service.domain.exception.DatoInvalidoException;
import com.facturacionAutonomo.customer_service.domain.model.Address;

@Service
public class RegisterAddressService implements RegisterAddressUseCase{

	private static final Logger log = LoggerFactory.getLogger(RegisterAddressService.class);

	private final AddressRepositoryPort addressRepository;

	public RegisterAddressService(AddressRepositoryPort addressRepository) {
		super();
		this.addressRepository = addressRepository;
	}

	@Override
	@Transactional
	public AddressResult execute(RegistrarAddressCommand command) {
		if (command.tenantId() == null) {
			throw new DatoInvalidoException("El tenant es obligatorio");
		}

		log.info("[RegisterAddressService] alta de direccion tenantId: {}", command.tenantId());

		// Regla: la última dirección dada de alta pasa a ser la fiscal (crearAddress nace con active = true).
		// Primero se desactiva la fiscal actual; si no, el índice único parcial rechazaría el INSERT.
		addressRepository.desactivarFiscal(command.tenantId());

		Address address = Address.crearAddress(command.calle(), command.postalCode(), command.city(),
				command.province(), command.country());

		Address guardado = addressRepository.save(command.tenantId(), address);

		return AddressResult.from(guardado);
	}

}
