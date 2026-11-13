package com.facturacionAutonomo.product_service.application.service.modificadores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.product_service.application.command.menuitemmodifier.UpdateMenuItemModifierCommand;
import com.facturacionAutonomo.product_service.application.port.OUT.MenuItemModifierRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.in.UpdateMenuItemModifierUseCase;
import com.facturacionAutonomo.product_service.application.result.MenuItemModifierResult;
import com.facturacionAutonomo.product_service.application.service.finder.ModifierFinder;
import com.facturacionAutonomo.product_service.domain.model.MenuItem;
import com.facturacionAutonomo.product_service.domain.model.MenuItemModifier;
import com.facturacionAutonomo.product_service.domain.model.Modifier;

@Service
public class UpdateMenuItemModifierService implements UpdateMenuItemModifierUseCase{

	private static final Logger log = LoggerFactory.getLogger(UpdateMenuItemModifierService.class);

	private final MenuItemModifierRepositoryPort menuitemmodifierrepository;
	private final ModifierFinder finder;


	public UpdateMenuItemModifierService(MenuItemModifierRepositoryPort menuitemmodifierrepository,
			ModifierFinder finder) {
		super();
		this.menuitemmodifierrepository = menuitemmodifierrepository;
		this.finder = finder;
	}


	@Override
	@Transactional
	public MenuItemModifierResult execute(UpdateMenuItemModifierCommand commando) {

		finder.tenantObligatorio(commando.tenantId());
		log.info("[UpdateMenuItemModifierService] reglas de un modificador de producto tenantId: {}", commando.tenantId());

		// Con el modificador activo, igual que en el listado: uno eliminado no aparece en el producto.
		MenuItem item = finder.productoActivo(commando.tenantId(), commando.menuItemPublicId());
		Modifier modifier = finder.modifierActivo(commando.tenantId(), commando.modifierPublicId());
		MenuItemModifier asociacion = finder.asociacion(item.getId(), modifier.getId());

		asociacion.updateRules(commando.maxSelections(), commando.required());
		return MenuItemModifierResult.convertirA(menuitemmodifierrepository.update(asociacion), modifier);
	}

}
