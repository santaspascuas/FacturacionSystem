package com.facturacionAutonomo.product_service.application.port.in.order;

import com.facturacionAutonomo.product_service.application.command.orders.AnadirLineasOrderCommand;
import com.facturacionAutonomo.product_service.application.result.OrderResult;

public interface AnadirLineasOrderUseCase {

	OrderResult execute(AnadirLineasOrderCommand command);

}
