package com.facturacionAutonomo.customer_service.application.service.address;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.customer_service.application.command.DeleteAddressCommand;
import com.facturacionAutonomo.customer_service.application.port.in.address.DeleteAddressUseCase;
import com.facturacionAutonomo.customer_service.application.port.out.AddressRepositoryPort;
import com.facturacionAutonomo.customer_service.domain.exception.AddressNoEncontradoException;
import com.facturacionAutonomo.customer_service.domain.exception.DatoInvalidoException;
import com.facturacionAutonomo.customer_service.domain.model.Address;

@Service
public class DeleteAddressService implements DeleteAddressUseCase{

	private static final Logger log = LoggerFactory.getLogger(DeleteAddressService.class);

	private final AddressRepositoryPort addressRepository;

	public DeleteAddressService(AddressRepositoryPort addressRepository) {
		super();
		this.addressRepository = addressRepository;
	}

	// Borrado físico. Las facturas copian los datos de la dirección, así que no dependen de esta fila.
	// Regla: si se borra la fiscal, pasa a fiscal la más antigua de las que quedan.
	@Override
	@Transactional
	public void execute(DeleteAddressCommand command) {
		if (command.tenantId() == null) {
			throw new DatoInvalidoException("El tenant es obligatorio");
		}

		log.info("[DeleteAddressService] borrar direccion publicId: {} tenantId: {}", command.publicId(),
				command.tenantId());

		// Hay que leerla antes de borrar: necesitamos saber si era la fiscal.
		Address borrar = addressRepository.findByPublicIdAndTenantId(command.publicId(), command.tenantId())
				.orElseThrow(() -> new AddressNoEncontradoException("La dirección no existe"));

		if (!addressRepository.deleteByPublicIdAndTenantId(command.publicId(), command.tenantId())) {
			throw new AddressNoEncontradoException("La dirección no existe");
		}

		if (borrar.isActive()) {
			addressRepository.findByTenantId(command.tenantId()).stream().findFirst().ifPresent(otra -> {
				log.info("[DeleteAddressService] nueva direccion fiscal publicId: {}", otra.getPublicId());
				addressRepository.save(command.tenantId(), otra.activar());
			});
		}
	}

}
