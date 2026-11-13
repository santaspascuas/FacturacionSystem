package com.facturacionAutonomo.product_service.application.port.in;

import com.facturacionAutonomo.product_service.application.command.menuitemmodifier.AsignarModifierCommand;
import com.facturacionAutonomo.product_service.application.result.MenuItemModifierResult;

public interface AsignarModifierUseCase {

	MenuItemModifierResult execute(AsignarModifierCommand commando);

}
