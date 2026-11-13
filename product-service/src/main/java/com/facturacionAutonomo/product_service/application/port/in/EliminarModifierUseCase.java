package com.facturacionAutonomo.product_service.application.port.in;

import com.facturacionAutonomo.product_service.application.command.modifier.EliminarModifierCommand;

public interface EliminarModifierUseCase {

	void execute(EliminarModifierCommand commando);

}
