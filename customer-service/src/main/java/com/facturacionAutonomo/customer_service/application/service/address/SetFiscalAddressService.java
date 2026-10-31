package com.facturacionAutonomo.customer_service.application.service.address;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.customer_service.application.command.SetFiscalAddressCommand;
import com.facturacionAutonomo.customer_service.application.port.in.address.SetFiscalAddressUseCase;
import com.facturacionAutonomo.customer_service.application.port.out.AddressRepositoryPort;
import com.facturacionAutonomo.customer_service.application.result.AddressResult;
import com.facturacionAutonomo.customer_service.domain.exception.AddressNoEncontradoException;
import com.facturacionAutonomo.customer_service.domain.model.Address;

@Service
public class SetFiscalAddressService implements SetFiscalAddressUseCase {

	private static final Logger log = LoggerFactory.getLogger(SetFiscalAddressService.class);

	private final AddressRepositoryPort addressRepository;

	public SetFiscalAddressService(AddressRepositoryPort addressRepository) {
		super();
		this.addressRepository = addressRepository;
	}

	// Desactivar y activar van en la misma transacción: nunca quedan dos fiscales ni ninguna a medias.
	@Override
	@Transactional
	public AddressResult execute(SetFiscalAddressCommand command) {
		log.info("[SetFiscalAddressService] marcar fiscal publicId: {} tenantId: {}", command.publicId(),
				command.tenantId());

		Address base = addressRepository.findByPublicIdAndTenantId(command.publicId(), command.tenantId())
				.orElseThrow(() -> new AddressNoEncontradoException("La dirección no existe"));

		// Ya es la fiscal: no hay nada que cambiar.
		if (base.isActive()) {
			return AddressResult.from(base);
		}

		addressRepository.desactivarFiscal(command.tenantId());
		Address guardado = addressRepository.save(command.tenantId(), base.activar());

		return AddressResult.from(guardado);
	}

}
