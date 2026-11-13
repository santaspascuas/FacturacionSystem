package com.facturacionAutonomo.product_service.application.service.modificadores;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.product_service.application.port.OUT.MenuItemModifierRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.OUT.ModifierRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.in.GetMenuItemModifiersUseCase;
import com.facturacionAutonomo.product_service.application.query.GetMenuItemModifiersQuery;
import com.facturacionAutonomo.product_service.application.result.MenuItemModifierResult;
import com.facturacionAutonomo.product_service.application.service.finder.ModifierFinder;
import com.facturacionAutonomo.product_service.domain.model.MenuItem;
import com.facturacionAutonomo.product_service.domain.model.MenuItemModifier;
import com.facturacionAutonomo.product_service.domain.model.Modifier;

@Service
public class GetMenuItemModifiersService implements GetMenuItemModifiersUseCase{

	private static final Logger log = LoggerFactory.getLogger(GetMenuItemModifiersService.class);

	private final MenuItemModifierRepositoryPort menuitemmodifierrepository;
	private final ModifierRepositoryPort modifierrepository;
	private final ModifierFinder finder;


	public GetMenuItemModifiersService(MenuItemModifierRepositoryPort menuitemmodifierrepository,
			ModifierRepositoryPort modifierrepository, ModifierFinder finder) {
		super();
		this.menuitemmodifierrepository = menuitemmodifierrepository;
		this.modifierrepository = modifierrepository;
		this.finder = finder;
	}


	@Override
	@Transactional(readOnly = true)
	public List<MenuItemModifierResult> execute(GetMenuItemModifiersQuery query) {

		finder.tenantObligatorio(query.tenantId());
		log.info("[GetMenuItemModifiersService] modificadores de un producto tenantId: {}", query.tenantId());

		MenuItem item = finder.productoActivo(query.tenantId(), query.menuItemPublicId());
		List<MenuItemModifier> asociaciones = menuitemmodifierrepository.findByMenuItemId(item.getId());

		// Una sola consulta para todos los modificadores, en vez de una por asociación (N+1).
		// Los eliminados no se ofrecen en el producto.
		Map<Long, Modifier> activos = modifierrepository
				.findByIds(asociaciones.stream().map(MenuItemModifier::getModifierId).toList()).stream()
				.filter(Modifier::isActive)
				.collect(Collectors.toMap(Modifier::getId, Function.identity()));

		return asociaciones.stream()
				.filter(asociacion -> activos.containsKey(asociacion.getModifierId()))
				.map(asociacion -> MenuItemModifierResult.convertirA(asociacion, activos.get(asociacion.getModifierId())))
				.toList();
	}

}
