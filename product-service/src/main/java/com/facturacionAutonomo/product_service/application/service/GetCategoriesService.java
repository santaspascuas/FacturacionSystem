package com.facturacionAutonomo.product_service.application.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.product_service.application.port.OUT.MenuCategoryRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.in.GetCategoriesUseCase;
import com.facturacionAutonomo.product_service.application.query.GetCategoriesQuery;
import com.facturacionAutonomo.product_service.application.result.CategoryResult;

@Service
public class GetCategoriesService implements GetCategoriesUseCase{

	private static final Logger log = LoggerFactory.getLogger(GetCategoriesService.class);

	private final MenuCategoryRepositoryPort menucategoryrepository;
	private final CategoryFinder finder;

	public GetCategoriesService(MenuCategoryRepositoryPort menucategoryrepository, CategoryFinder finder) {
		super();
		this.menucategoryrepository = menucategoryrepository;
		this.finder = finder;
	}

	@Override
	@Transactional(readOnly = true)
	public List<CategoryResult> execute(GetCategoriesQuery query) {
		finder.tenantObligatorio(query.tenantId());
		log.info("[GetCategoriesService] listar categorias tenantId: {}", query.tenantId());
		return menucategoryrepository.findActiveByTenantId(query.tenantId()).stream()
				.map(CategoryResult::convertirA).toList();
	}

}
