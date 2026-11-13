package com.facturacionAutonomo.product_service.application.service;

import java.time.Clock;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.product_service.application.command.menuitem.EliminarMenuItemCommand;
import com.facturacionAutonomo.product_service.application.port.OUT.MenuItemRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.in.EliminarMenuItemUseCase;
import com.facturacionAutonomo.product_service.application.service.finder.MenuItemFinder;
import com.facturacionAutonomo.product_service.domain.model.MenuItem;

@Service
public class EliminarMenuItemService implements EliminarMenuItemUseCase{

	private static final Logger log = LoggerFactory.getLogger(EliminarMenuItemService.class);

	private final MenuItemRepositoryPort menuitemrepository;
	private final MenuItemFinder finder;
	private final Clock clock;


	public EliminarMenuItemService(MenuItemRepositoryPort menuitemrepository, MenuItemFinder finder, Clock clock) {
		super();
		this.menuitemrepository = menuitemrepository;
		this.finder = finder;
		this.clock = clock;
	}


	@Override
	@Transactional
	public void execute(EliminarMenuItemCommand commando) {

		finder.tenantObligatorio(commando.tenantId());
		log.info("[EliminarMenuItemService] baja de producto tenantId: {}", commando.tenantId());

		// Un producto ya eliminado cuenta como no encontrado, igual que uno de otro tenant.
		MenuItem item = finder.productoActivo(commando.tenantId(), commando.publicId());
		item.eliminar(LocalDateTime.now(clock));
		menuitemrepository.update(item);
	}

}
