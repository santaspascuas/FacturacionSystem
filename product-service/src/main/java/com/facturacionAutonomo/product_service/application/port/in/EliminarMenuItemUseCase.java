package com.facturacionAutonomo.product_service.application.port.in;

import com.facturacionAutonomo.product_service.application.command.menuitem.EliminarMenuItemCommand;

public interface EliminarMenuItemUseCase {

	void execute(EliminarMenuItemCommand commando);

}
