package com.facturacionAutonomo.product_service.application.port.in.order;

import java.util.List;

import com.facturacionAutonomo.product_service.application.query.GetOrdersQuery;
import com.facturacionAutonomo.product_service.application.result.OrderResult;

public interface GetOrdersUseCase {

	List<OrderResult> execute(GetOrdersQuery query);

}
