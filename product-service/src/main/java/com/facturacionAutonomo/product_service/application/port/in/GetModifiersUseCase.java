package com.facturacionAutonomo.product_service.application.port.in;

import java.util.List;

import com.facturacionAutonomo.product_service.application.query.GetModifiersQuery;
import com.facturacionAutonomo.product_service.application.result.ModifierResult;

public interface GetModifiersUseCase {

	List<ModifierResult> execute(GetModifiersQuery query);

}
