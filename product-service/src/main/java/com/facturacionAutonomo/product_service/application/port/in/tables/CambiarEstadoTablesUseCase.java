package com.facturacionAutonomo.product_service.application.port.in.tables;

import com.facturacionAutonomo.product_service.application.command.tables.CambiarEstadoTablesCommand;
import com.facturacionAutonomo.product_service.application.result.TablesResult;

public interface CambiarEstadoTablesUseCase {

	TablesResult execute(CambiarEstadoTablesCommand command);

}
