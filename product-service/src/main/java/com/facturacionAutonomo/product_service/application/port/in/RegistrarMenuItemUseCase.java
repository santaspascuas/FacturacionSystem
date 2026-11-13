package com.facturacionAutonomo.product_service.application.port.in;

import com.facturacionAutonomo.product_service.application.command.menuitem.RegistrarMenuItemCommand;
import com.facturacionAutonomo.product_service.application.result.MenuItemResult;

public interface RegistrarMenuItemUseCase {

	MenuItemResult execute(RegistrarMenuItemCommand commando);

}
