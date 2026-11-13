package com.facturacionAutonomo.product_service.application.service;

import java.time.Clock;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.product_service.application.command.menuitem.UpdateRegistrarMenuItemCommand;
import com.facturacionAutonomo.product_service.application.port.OUT.MenuItemRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.in.UpdateMenuItemUseCase;
import com.facturacionAutonomo.product_service.application.result.MenuItemResult;
import com.facturacionAutonomo.product_service.application.service.finder.MenuItemFinder;
import com.facturacionAutonomo.product_service.domain.exception.MenuItemYaRegistradoException;
import com.facturacionAutonomo.product_service.domain.model.MenuCategory;
import com.facturacionAutonomo.product_service.domain.model.MenuItem;

@Service
public class UpdateMenuItemsService implements UpdateMenuItemUseCase{

	private static final Logger log = LoggerFactory.getLogger(UpdateMenuItemsService.class);
	private final MenuItemRepositoryPort menuitemrepository;
	private final MenuItemFinder finder;
	private final Clock clock;


	public UpdateMenuItemsService(MenuItemRepositoryPort menuitemrepository, MenuItemFinder finder, Clock clock) {
		super();
		this.menuitemrepository = menuitemrepository;
		this.finder = finder;
		this.clock = clock;
	}


	@Override
	@Transactional
	public MenuItemResult execute(UpdateRegistrarMenuItemCommand commando) {
		finder.tenantObligatorio(commando.tenantId());
		log.info("[UpdateMenuItemsService] modificación de producto tenantId: {}", commando.tenantId());

		String name = MenuItem.normalizarNombre(commando.name());
		MenuItem.precioValido(commando.basePrice());

		// Se buscan también los inactivos: modificar es la forma de volver a activar un producto eliminado.
		MenuItem item = finder.productoDelTenant(commando.tenantId(), commando.menuItemPublicId());

		//Si el usuario selecciono cambiar de categoria.Ej:cafe pasa a bebidas calientes. Entonces le paso el uuid de la categoria
		MenuCategory categoria = finder.categoriaDelProducto(commando.tenantId(), commando.categoryPublicId());

		validateNameAvailable(item, name, commando.isactive());

		LocalDateTime ahora = LocalDateTime.now(clock);
		item.updateDetails(categoria.getId(), name, MenuItem.textoOpcional(commando.description()),
				MenuItem.textoOpcional(commando.imageUrl()), commando.isactive(), ahora);
		item.changePrice(commando.basePrice(), ahora);

		// update y no save: save crearía una entidad nueva y haría un INSERT.
		MenuItem guardado = menuitemrepository.update(item);
		return MenuItemResult.convertirA(guardado);
	}


	// Solo se comprueba si el producto queda activo con un nombre que no tenía como activo:
	// si ya era activo con ese nombre, la consulta lo encontraría a él mismo.
	private void validateNameAvailable(MenuItem item, String name, boolean active) {
		boolean sameActiveName = item.isActive() && name.equals(item.getName());
		if(active && !sameActiveName && menuitemrepository.existsActiveByTenantIdAndName(item.getTenantId(), name)) {
			throw new MenuItemYaRegistradoException("El producto " + name + " ya está registrado");
		}
	}

}
