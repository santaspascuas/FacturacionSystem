package com.facturacionAutonomo.product_service.application.port.in.payment;

import java.util.List;

import com.facturacionAutonomo.product_service.application.query.GetPaymentsQuery;
import com.facturacionAutonomo.product_service.application.result.PaymentResult;

public interface GetPaymentsUseCase {

	List<PaymentResult> execute(GetPaymentsQuery query);

}
