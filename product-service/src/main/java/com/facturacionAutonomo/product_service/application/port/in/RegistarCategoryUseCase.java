package com.facturacionAutonomo.product_service.application.port.in;

import com.facturacionAutonomo.product_service.application.command.category.RegistrarCategoryCommand;
import com.facturacionAutonomo.product_service.application.result.CategoryResult;

public interface RegistarCategoryUseCase {
	
	CategoryResult execute(RegistrarCategoryCommand commando);

}
