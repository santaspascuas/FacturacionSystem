package com.facturacionAutonomo.product_service.application.port.in.order;

import com.facturacionAutonomo.product_service.application.command.orders.UpdateLineaOrderCommand;
import com.facturacionAutonomo.product_service.application.result.OrderResult;

public interface UpdateLineaOrderUseCase {

	OrderResult execute(UpdateLineaOrderCommand command);

}
