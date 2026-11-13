package com.facturacionAutonomo.product_service.application.port.in.tables;

import java.util.List;

import com.facturacionAutonomo.product_service.application.query.GetTablesQuery;
import com.facturacionAutonomo.product_service.application.result.TablesResult;

public interface GetTablesUseCase {

	List<TablesResult> execute(GetTablesQuery query);

}
