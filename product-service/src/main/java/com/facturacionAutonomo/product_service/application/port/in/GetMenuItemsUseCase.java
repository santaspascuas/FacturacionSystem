package com.facturacionAutonomo.product_service.application.port.in;

import java.util.List;

import com.facturacionAutonomo.product_service.application.query.GetMenuItemsQuery;
import com.facturacionAutonomo.product_service.application.result.MenuItemResult;

public interface GetMenuItemsUseCase {

	List<MenuItemResult> execute(GetMenuItemsQuery query);

}
