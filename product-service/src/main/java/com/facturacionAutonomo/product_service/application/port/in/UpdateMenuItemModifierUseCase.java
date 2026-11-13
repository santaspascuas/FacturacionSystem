package com.facturacionAutonomo.product_service.application.port.in;

import com.facturacionAutonomo.product_service.application.command.menuitemmodifier.UpdateMenuItemModifierCommand;
import com.facturacionAutonomo.product_service.application.result.MenuItemModifierResult;

public interface UpdateMenuItemModifierUseCase {

	MenuItemModifierResult execute(UpdateMenuItemModifierCommand commando);

}
