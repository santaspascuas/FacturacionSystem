package com.facturacionAutonomo.product_service.application.port.in.order;

import com.facturacionAutonomo.product_service.application.command.orders.UpdateOrderCommand;
import com.facturacionAutonomo.product_service.application.result.OrderResult;

public interface UpdateOrderUseCase {

	OrderResult execute(UpdateOrderCommand command);

}
