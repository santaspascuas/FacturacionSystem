package com.facturacionAutonomo.product_service.application.port.in.tables;

import com.facturacionAutonomo.product_service.application.command.tables.EliminarTablesCommand;

public interface EliminarTablesUseCase {

	void execute(EliminarTablesCommand command);

}
