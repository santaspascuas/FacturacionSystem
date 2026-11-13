package com.facturacionAutonomo.product_service.application.port.in.order;

import com.facturacionAutonomo.product_service.application.command.orders.RegistrarOrderCommand;
import com.facturacionAutonomo.product_service.application.result.OrderResult;

public interface RegistrarOrderUseCase {

	OrderResult execute(RegistrarOrderCommand command);

}
