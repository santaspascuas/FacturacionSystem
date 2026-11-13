package com.facturacionAutonomo.product_service.application.port.in;

import com.facturacionAutonomo.product_service.application.command.menuitem.UpdateRegistrarMenuItemCommand;
import com.facturacionAutonomo.product_service.application.result.MenuItemResult;

public interface UpdateMenuItemUseCase {

	MenuItemResult execute(UpdateRegistrarMenuItemCommand commando);

}
