package com.facturacionAutonomo.product_service.application.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.product_service.application.command.menuitem.RegistrarMenuItemCommand;
import com.facturacionAutonomo.product_service.application.port.OUT.MenuItemRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.in.RegistrarMenuItemUseCase;
import com.facturacionAutonomo.product_service.application.result.MenuItemResult;
import com.facturacionAutonomo.product_service.application.service.finder.MenuItemFinder;
import com.facturacionAutonomo.product_service.domain.exception.MenuItemYaRegistradoException;
import com.facturacionAutonomo.product_service.domain.model.MenuCategory;
import com.facturacionAutonomo.product_service.domain.model.MenuItem;

@Service
public class RegistrarMenuItemService implements RegistrarMenuItemUseCase{

	private static final Logger log = LoggerFactory.getLogger(RegistrarMenuItemService.class);

	private final MenuItemRepositoryPort menuitemrepository;
	private final MenuItemFinder finder;
	private final Clock clock;


	public RegistrarMenuItemService(MenuItemRepositoryPort menuitemrepository, MenuItemFinder finder, Clock clock) {
		super();
		this.menuitemrepository = menuitemrepository;
		this.finder = finder;
		this.clock = clock;
	}


	@Override
	@Transactional
	public MenuItemResult execute(RegistrarMenuItemCommand commando) {

		finder.tenantObligatorio(commando.tenantId());
		String name = MenuItem.normalizarNombre(commando.name());
		MenuItem.precioValido(commando.basePrice());
		log.info("[RegistrarMenuItemService] alta de producto tenantId: {}", commando.tenantId());

		// La categoría tiene que ser del mismo tenant: si es de otro, para este tenant no existe.
		MenuCategory categoria = finder.categoriaDelProducto(commando.tenantId(), commando.categoryPublicId());
		validarNombreLibre(commando.tenantId(), name);

		MenuItem item = MenuItem.crearMenuItem(commando.tenantId(), categoria.getId(), name,
				MenuItem.textoOpcional(commando.description()), commando.basePrice(),
				MenuItem.textoOpcional(commando.imageUrl()), LocalDateTime.now(clock));

		MenuItem guardado = menuitemrepository.save(item);
		return MenuItemResult.convertirA(guardado);
	}


	// Solo cuenta los activos: un producto eliminado no impide volver a darlo de alta.
	private void validarNombreLibre(UUID tenantId, String name) {
		if(menuitemrepository.existsActiveByTenantIdAndName(tenantId, name)) {
			throw new MenuItemYaRegistradoException("El producto " + name + " ya está registrado");
		}
	}

}
