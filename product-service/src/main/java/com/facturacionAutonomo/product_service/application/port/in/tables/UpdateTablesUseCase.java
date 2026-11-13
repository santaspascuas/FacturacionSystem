package com.facturacionAutonomo.product_service.application.port.in.tables;

import com.facturacionAutonomo.product_service.application.command.tables.UpdateTablesCommand;
import com.facturacionAutonomo.product_service.application.result.TablesResult;

public interface UpdateTablesUseCase {
	
	TablesResult execute(UpdateTablesCommand commando);
	

}
