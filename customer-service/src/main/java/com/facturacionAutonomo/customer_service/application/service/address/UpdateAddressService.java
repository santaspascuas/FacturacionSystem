package com.facturacionAutonomo.customer_service.application.service.address;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.customer_service.application.command.UpdateAddressCommand;
import com.facturacionAutonomo.customer_service.application.port.in.address.UpdateAddressUseCase;
import com.facturacionAutonomo.customer_service.application.port.out.AddressRepositoryPort;
import com.facturacionAutonomo.customer_service.application.result.AddressResult;
import com.facturacionAutonomo.customer_service.domain.exception.AddressNoEncontradoException;
import com.facturacionAutonomo.customer_service.domain.model.Address;

@Service
public class UpdateAddressService implements UpdateAddressUseCase {

	private static final Logger log = LoggerFactory.getLogger(UpdateAddressService.class);

	private final AddressRepositoryPort addressRepository;

	public UpdateAddressService(AddressRepositoryPort addressRepository) {
		super();
		this.addressRepository = addressRepository;
	}

	
	
	@Override
	@Transactional
	public AddressResult execute(UpdateAddressCommand command) {
		log.info("[UpdateAddressService] actualizar direccion publicId: {} tenantId: {}", command.publicId(),
				command.tenantId());

		Address base = addressRepository.findByPublicIdAndTenantId(command.publicId(), command.tenantId())
				.orElseThrow(() -> new AddressNoEncontradoException("La dirección no existe"));

		Address actualizado = base.actualizarDatos(command.calle(), command.postalCode(), command.city(),
				command.province(), command.country());

		Address guardado = addressRepository.save(command.tenantId(), actualizado);

		return AddressResult.from(guardado);
	}

}
