package com.facturacionAutonomo.product_service.application.port.in;

import com.facturacionAutonomo.product_service.application.command.modifier.RegistrarModifierCommand;
import com.facturacionAutonomo.product_service.application.result.ModifierResult;

public interface RegistrarModifierUseCase {

	ModifierResult execute(RegistrarModifierCommand commando);

}
