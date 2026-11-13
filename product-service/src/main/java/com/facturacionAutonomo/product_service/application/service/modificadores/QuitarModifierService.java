package com.facturacionAutonomo.product_service.application.service.modificadores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.product_service.application.command.menuitemmodifier.QuitarModifierCommand;
import com.facturacionAutonomo.product_service.application.port.OUT.MenuItemModifierRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.in.QuitarModifierUseCase;
import com.facturacionAutonomo.product_service.application.service.finder.ModifierFinder;
import com.facturacionAutonomo.product_service.domain.model.MenuItem;
import com.facturacionAutonomo.product_service.domain.model.MenuItemModifier;
import com.facturacionAutonomo.product_service.domain.model.Modifier;

@Service
public class QuitarModifierService implements QuitarModifierUseCase{

	private static final Logger log = LoggerFactory.getLogger(QuitarModifierService.class);

	private final MenuItemModifierRepositoryPort menuitemmodifierrepository;
	private final ModifierFinder finder;


	public QuitarModifierService(MenuItemModifierRepositoryPort menuitemmodifierrepository, ModifierFinder finder) {
		super();
		this.menuitemmodifierrepository = menuitemmodifierrepository;
		this.finder = finder;
	}


	@Override
	@Transactional
	public void execute(QuitarModifierCommand commando) {

		finder.tenantObligatorio(commando.tenantId());
		log.info("[QuitarModifierService] quitar modificador de un producto tenantId: {}", commando.tenantId());

		// El modificador puede estar eliminado: quitar su asociación sigue siendo válido.
		MenuItem item = finder.productoActivo(commando.tenantId(), commando.menuItemPublicId());
		Modifier modifier = finder.modifierDelTenant(commando.tenantId(), commando.modifierPublicId());
		MenuItemModifier asociacion = finder.asociacion(item.getId(), modifier.getId());

		menuitemmodifierrepository.deleteById(asociacion.getId());
	}

}
