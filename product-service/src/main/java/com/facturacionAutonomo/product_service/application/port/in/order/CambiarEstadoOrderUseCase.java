package com.facturacionAutonomo.product_service.application.port.in.order;

import com.facturacionAutonomo.product_service.application.command.orders.CambiarEstadoOrderCommand;
import com.facturacionAutonomo.product_service.application.result.OrderResult;

public interface CambiarEstadoOrderUseCase {

	OrderResult execute(CambiarEstadoOrderCommand command);

}
