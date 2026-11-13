package com.facturacionAutonomo.product_service.application.port.in;

import com.facturacionAutonomo.product_service.application.command.category.UpdateCategoryCommand;
import com.facturacionAutonomo.product_service.application.result.CategoryResult;

public interface UpdateCategoryUseCase {
	
	CategoryResult execute (UpdateCategoryCommand comando);

}
