package com.facturacionAutonomo.product_service.application.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.product_service.application.port.OUT.MenuItemRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.in.GetMenuItemsUseCase;
import com.facturacionAutonomo.product_service.application.query.GetMenuItemsQuery;
import com.facturacionAutonomo.product_service.application.result.MenuItemResult;
import com.facturacionAutonomo.product_service.application.service.finder.MenuItemFinder;
import com.facturacionAutonomo.product_service.domain.model.MenuCategory;
import com.facturacionAutonomo.product_service.domain.model.MenuItem;

@Service
public class GetMenuItemsService implements GetMenuItemsUseCase{

	private static final Logger log = LoggerFactory.getLogger(GetMenuItemsService.class);

	private final MenuItemRepositoryPort menuitemrepository;
	private final MenuItemFinder finder;


	public GetMenuItemsService(MenuItemRepositoryPort menuitemrepository, MenuItemFinder finder) {
		super();
		this.menuitemrepository = menuitemrepository;
		this.finder = finder;
	}


	@Override
	@Transactional(readOnly = true)
	public List<MenuItemResult> execute(GetMenuItemsQuery query) {

		finder.tenantObligatorio(query.tenantId());
		log.info("[GetMenuItemsService] listado de productos tenantId: {}", query.tenantId());

		return buscarProductos(query).stream().map(MenuItemResult::convertirA).toList();
	}


	// Sin categoría se lista toda la carta; con ella, solo los productos de esa categoría del tenant.
	private List<MenuItem> buscarProductos(GetMenuItemsQuery query) {
		if(query.categoryPublicId() == null) {
			return menuitemrepository.findActiveByTenantId(query.tenantId());
		}
		MenuCategory categoria = finder.categoriaDelProducto(query.tenantId(), query.categoryPublicId());
		return menuitemrepository.findActiveByTenantIdAndCategoryId(query.tenantId(), categoria.getId());
	}

}
