package com.facturacionAutonomo.product_service.application.port.in;

import java.util.List;

import com.facturacionAutonomo.product_service.application.query.GetMenuItemModifiersQuery;
import com.facturacionAutonomo.product_service.application.result.MenuItemModifierResult;

public interface GetMenuItemModifiersUseCase {

	List<MenuItemModifierResult> execute(GetMenuItemModifiersQuery query);

}
