package com.facturacionAutonomo.product_service.application.port.in.tables;

import com.facturacionAutonomo.product_service.application.command.tables.RegistrarTablesCommand;
import com.facturacionAutonomo.product_service.application.result.TablesResult;

public interface RegistrarTablesUseCase {
	
	TablesResult execute (RegistrarTablesCommand command);

}
