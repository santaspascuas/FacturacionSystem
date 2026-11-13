package com.facturacionAutonomo.product_service.application.port.in.order;

import com.facturacionAutonomo.product_service.application.query.GetOrderQuery;
import com.facturacionAutonomo.product_service.application.result.OrderResult;

public interface GetOrderUseCase {

	OrderResult execute(GetOrderQuery query);

}
