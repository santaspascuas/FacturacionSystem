package com.facturacionAutonomo.product_service.application.port.in;

import com.facturacionAutonomo.product_service.application.command.modifier.UpdateModifierCommand;
import com.facturacionAutonomo.product_service.application.result.ModifierResult;

public interface UpdateModifierUseCase {

	ModifierResult execute(UpdateModifierCommand commando);

}
