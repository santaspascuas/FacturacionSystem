package com.facturacionAutonomo.product_service.application.port.in;

import java.util.List;

import com.facturacionAutonomo.product_service.application.query.GetCategoriesQuery;
import com.facturacionAutonomo.product_service.application.result.CategoryResult;

public interface GetCategoriesUseCase {

	List<CategoryResult> execute(GetCategoriesQuery query);

}
