package com.facturacionAutonomo.product_service.application.port.in.payment;

import com.facturacionAutonomo.product_service.application.command.payments.RegistrarPaymentCommand;
import com.facturacionAutonomo.product_service.application.result.RegistrarPaymentResult;

public interface RegistrarPaymentUseCase {

	RegistrarPaymentResult execute(RegistrarPaymentCommand command);

}
