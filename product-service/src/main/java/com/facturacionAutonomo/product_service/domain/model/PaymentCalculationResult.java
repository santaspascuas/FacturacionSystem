package com.facturacionAutonomo.product_service.domain.model;

import java.math.BigDecimal;

public record PaymentCalculationResult(
		BigDecimal aplicado, 
	    BigDecimal efectivo, 
	    BigDecimal cambio
		) {

}
