package com.facturacionAutonomo.documentacionfactura_service.application.port.in;

import com.facturacionAutonomo.documentacionfactura_service.application.query.GetOrderQuery;
import com.facturacionAutonomo.documentacionfactura_service.application.result.OrderResult;

public interface GetOrderUseCase {
	
	OrderResult execute(GetOrderQuery query);

}
