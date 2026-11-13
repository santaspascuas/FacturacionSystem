package com.facturacionAutonomo.product_service.application.port.in;

import com.facturacionAutonomo.product_service.application.command.menuitemmodifier.QuitarModifierCommand;

public interface QuitarModifierUseCase {

	void execute(QuitarModifierCommand commando);

}
