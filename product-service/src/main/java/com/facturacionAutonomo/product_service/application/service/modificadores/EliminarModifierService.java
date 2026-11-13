package com.facturacionAutonomo.product_service.application.service.modificadores;

import java.time.Clock;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.product_service.application.command.modifier.EliminarModifierCommand;
import com.facturacionAutonomo.product_service.application.port.OUT.ModifierRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.in.EliminarModifierUseCase;
import com.facturacionAutonomo.product_service.application.service.finder.ModifierFinder;
import com.facturacionAutonomo.product_service.domain.model.Modifier;

@Service
public class EliminarModifierService implements EliminarModifierUseCase{

	private static final Logger log = LoggerFactory.getLogger(EliminarModifierService.class);

	private final ModifierRepositoryPort modifierrepository;
	private final ModifierFinder finder;
	private final Clock clock;


	public EliminarModifierService(ModifierRepositoryPort modifierrepository, ModifierFinder finder, Clock clock) {
		super();
		this.modifierrepository = modifierrepository;
		this.finder = finder;
		this.clock = clock;
	}


	// Borrado lógico. Sus asociaciones a productos se conservan: dejan de listarse mientras esté inactivo
	// y vuelven si se reactiva.
	@Override
	@Transactional
	public void execute(EliminarModifierCommand commando) {

		finder.tenantObligatorio(commando.tenantId());
		log.info("[EliminarModifierService] baja de modificador tenantId: {}", commando.tenantId());

		Modifier modifier = finder.modifierActivo(commando.tenantId(), commando.publicId());
		modifier.eliminar(LocalDateTime.now(clock));
		modifierrepository.update(modifier);
	}

}
