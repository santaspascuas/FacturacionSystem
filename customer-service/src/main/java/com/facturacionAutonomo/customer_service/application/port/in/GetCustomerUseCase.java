package com.facturacionAutonomo.customer_service.application.port.in;

import com.facturacionAutonomo.customer_service.application.query.GetCustomerQuery;
import com.facturacionAutonomo.customer_service.application.result.CustomerResult;

public interface GetCustomerUseCase {

	CustomerResult execute(GetCustomerQuery query);
}
