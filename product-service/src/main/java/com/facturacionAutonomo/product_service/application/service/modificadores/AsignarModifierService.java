package com.facturacionAutonomo.product_service.application.service.modificadores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.product_service.application.command.menuitemmodifier.AsignarModifierCommand;
import com.facturacionAutonomo.product_service.application.port.OUT.MenuItemModifierRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.in.AsignarModifierUseCase;
import com.facturacionAutonomo.product_service.application.result.MenuItemModifierResult;
import com.facturacionAutonomo.product_service.application.service.finder.ModifierFinder;
import com.facturacionAutonomo.product_service.domain.exception.ModifierYaAsignadoException;
import com.facturacionAutonomo.product_service.domain.model.MenuItem;
import com.facturacionAutonomo.product_service.domain.model.MenuItemModifier;
import com.facturacionAutonomo.product_service.domain.model.Modifier;

@Service
public class AsignarModifierService implements AsignarModifierUseCase{

	private static final Logger log = LoggerFactory.getLogger(AsignarModifierService.class);

	// DEFAULT de menu_item_modifiers.
	private static final int MAX_SELECTIONS_POR_DEFECTO = 1;
	private static final boolean REQUIRED_POR_DEFECTO = false;

	private final MenuItemModifierRepositoryPort menuitemmodifierrepository;
	private final ModifierFinder finder;


	public AsignarModifierService(MenuItemModifierRepositoryPort menuitemmodifierrepository, ModifierFinder finder) {
		super();
		this.menuitemmodifierrepository = menuitemmodifierrepository;
		this.finder = finder;
	}


	@Override
	@Transactional
	public MenuItemModifierResult execute(AsignarModifierCommand commando) {

		finder.tenantObligatorio(commando.tenantId());
		log.info("[AsignarModifierService] asignación de modificador tenantId: {}", commando.tenantId());

		// Los dos se buscan con el mismo tenant: así no se puede asociar un modificador de otro restaurante,
		// que la BD no impide.
		MenuItem item = finder.productoActivo(commando.tenantId(), commando.menuItemPublicId());
		Modifier modifier = finder.modifierActivo(commando.tenantId(), commando.modifierPublicId());
		validarNoAsignado(item, modifier);

		int maxSelections = commando.maxSelections() == null ? MAX_SELECTIONS_POR_DEFECTO : commando.maxSelections();
		boolean required = commando.required() == null ? REQUIRED_POR_DEFECTO : commando.required();
		MenuItemModifier asociacion = MenuItemModifier.crearMenuItemModifier(item.getId(), modifier.getId(),
				maxSelections, required);

		return MenuItemModifierResult.convertirA(menuitemmodifierrepository.save(asociacion), modifier);
	}


	private void validarNoAsignado(MenuItem item, Modifier modifier) {
		if(menuitemmodifierrepository.existsByMenuItemIdAndModifierId(item.getId(), modifier.getId())) {
			throw new ModifierYaAsignadoException("El modificador " + modifier.getName() + " ya está asociado a este producto");
		}
	}

}
