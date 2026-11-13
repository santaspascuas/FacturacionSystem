package com.facturacionAutonomo.product_service.application.service.modificadores;

import java.time.Clock;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.product_service.application.command.modifier.UpdateModifierCommand;
import com.facturacionAutonomo.product_service.application.port.OUT.ModifierRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.in.UpdateModifierUseCase;
import com.facturacionAutonomo.product_service.application.result.ModifierResult;
import com.facturacionAutonomo.product_service.application.service.finder.ModifierFinder;
import com.facturacionAutonomo.product_service.domain.exception.ModifierYaRegistradoException;
import com.facturacionAutonomo.product_service.domain.model.Modifier;

@Service
public class UpdateModifierService implements UpdateModifierUseCase{

	private static final Logger log = LoggerFactory.getLogger(UpdateModifierService.class);

	private final ModifierRepositoryPort modifierrepository;
	private final ModifierFinder finder;
	private final Clock clock;


	public UpdateModifierService(ModifierRepositoryPort modifierrepository, ModifierFinder finder, Clock clock) {
		super();
		this.modifierrepository = modifierrepository;
		this.finder = finder;
		this.clock = clock;
	}


	@Override
	@Transactional
	public ModifierResult execute(UpdateModifierCommand commando) {

		finder.tenantObligatorio(commando.tenantId());
		log.info("[UpdateModifierService] modificación de modificador tenantId: {}", commando.tenantId());

		Modifier modifier = finder.modifierDelTenant(commando.tenantId(), commando.modifierPublicId());
		String name = Modifier.normalizarNombre(commando.name());
		validarNombreLibre(modifier, name);

		LocalDateTime ahora = LocalDateTime.now(clock);
		modifier.updateDetails(name, commando.modifierType(), commando.isactive(), ahora);
		modifier.changePrice(commando.priceDelta(), ahora);

		// update y no save: save crearía una entidad nueva y haría un INSERT.
		return ModifierResult.convertirA(modifierrepository.update(modifier));
	}


	// Solo se comprueba si cambia el nombre: con el mismo, la consulta lo encontraría a él mismo.
	private void validarNombreLibre(Modifier modifier, String name) {
		if(!name.equals(modifier.getName()) && modifierrepository.existsByTenantIdAndName(modifier.getTenantId(), name)) {
			throw new ModifierYaRegistradoException("El modificador " + name + " ya está registrado");
		}
	}

}
