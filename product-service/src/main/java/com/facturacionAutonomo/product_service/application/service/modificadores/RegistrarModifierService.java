package com.facturacionAutonomo.product_service.application.service.modificadores;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.product_service.application.command.modifier.RegistrarModifierCommand;
import com.facturacionAutonomo.product_service.application.port.OUT.ModifierRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.in.RegistrarModifierUseCase;
import com.facturacionAutonomo.product_service.application.result.ModifierResult;
import com.facturacionAutonomo.product_service.application.service.finder.ModifierFinder;
import com.facturacionAutonomo.product_service.domain.exception.ModifierYaRegistradoException;
import com.facturacionAutonomo.product_service.domain.model.Modifier;

@Service
public class RegistrarModifierService implements RegistrarModifierUseCase{

	private static final Logger log = LoggerFactory.getLogger(RegistrarModifierService.class);

	// DEFAULT de modifiers.price_delta.
	private static final BigDecimal PRECIO_POR_DEFECTO = new BigDecimal("0.00");

	private final ModifierRepositoryPort modifierrepository;
	private final ModifierFinder finder;
	private final Clock clock;


	public RegistrarModifierService(ModifierRepositoryPort modifierrepository, ModifierFinder finder, Clock clock) {
		super();
		this.modifierrepository = modifierrepository;
		this.finder = finder;
		this.clock = clock;
	}


	@Override
	@Transactional
	public ModifierResult execute(RegistrarModifierCommand commando) {

		finder.tenantObligatorio(commando.tenantId());
		log.info("[RegistrarModifierService] alta de modificador tenantId: {}", commando.tenantId());

		String name = Modifier.normalizarNombre(commando.name());
		validarNombreLibre(commando.tenantId(), name);

		BigDecimal priceDelta = commando.priceDelta() == null ? PRECIO_POR_DEFECTO : commando.priceDelta();
		Modifier modifier = Modifier.crearModifier(commando.tenantId(), name, commando.modifierType(), priceDelta,
				LocalDateTime.now(clock));

		return ModifierResult.convertirA(modifierrepository.save(modifier));
	}


	// El UNIQUE (tenant_id, name) cuenta también los eliminados: un nombre de un modificador eliminado
	// no se reutiliza; ese modificador se reactiva modificándolo.
	private void validarNombreLibre(UUID tenantId, String name) {
		if(modifierrepository.existsByTenantIdAndName(tenantId, name)) {
			throw new ModifierYaRegistradoException("El modificador " + name + " ya está registrado");
		}
	}

}
