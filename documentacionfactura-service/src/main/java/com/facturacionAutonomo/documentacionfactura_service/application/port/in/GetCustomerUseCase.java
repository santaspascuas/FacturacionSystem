package com.facturacionAutonomo.documentacionfactura_service.application.port.in;

import com.facturacionAutonomo.documentacionfactura_service.application.query.GetCustomerQuery;
import com.facturacionAutonomo.documentacionfactura_service.application.result.CustomerResult;

/** Datos fiscales del restaurante (el emisor del ticket), leídos de customer-service. */
public interface GetCustomerUseCase {

	CustomerResult execute(GetCustomerQuery query);
}
