package com.facturacionAutonomo.product_service.application.port.in.order;

import com.facturacionAutonomo.product_service.application.command.orders.EliminarLineaOrderCommand;
import com.facturacionAutonomo.product_service.application.result.OrderResult;

public interface EliminarLineaOrderUseCase {

	OrderResult execute(EliminarLineaOrderCommand command);

}
