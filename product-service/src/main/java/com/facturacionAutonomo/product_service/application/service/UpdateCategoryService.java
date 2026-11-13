package com.facturacionAutonomo.product_service.application.service;

import java.time.Clock;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.product_service.application.command.category.UpdateCategoryCommand;
import com.facturacionAutonomo.product_service.application.port.OUT.MenuCategoryRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.in.UpdateCategoryUseCase;
import com.facturacionAutonomo.product_service.application.result.CategoryResult;
import com.facturacionAutonomo.product_service.domain.exception.CategoryYaRegistradoException;
import com.facturacionAutonomo.product_service.domain.model.MenuCategory;

@Service
public class UpdateCategoryService implements UpdateCategoryUseCase{

	private static final Logger log = LoggerFactory.getLogger(UpdateCategoryService.class);

	private final MenuCategoryRepositoryPort menucategoryrepository;
	private final CategoryFinder finder;
	private final Clock clock;


	public UpdateCategoryService(MenuCategoryRepositoryPort menucategoryrepository, CategoryFinder finder,
			Clock clock) {
		super();
		this.menucategoryrepository = menucategoryrepository;
		this.finder = finder;
		this.clock = clock;
	}


	@Override
	@Transactional
	public CategoryResult execute(UpdateCategoryCommand comando) {
		finder.tenantObligatorio(comando.tenantId());
		String nombre = MenuCategory.normalizarNombre(comando.name());

		log.info("[UpdateCategory] modificación con el tenantId :{}", comando.tenantId());

		MenuCategory categoria = finder.categoriaDelTenant(comando.tenantId(), comando.publicId());

		// Solo se comprueba si cambia el nombre: si no, se encontraría a sí misma.
		if(!nombre.equals(categoria.getName()) && menucategoryrepository.existsByTenantIdAndName(comando.tenantId(), nombre)) {
			throw new CategoryYaRegistradoException("La categoría " + nombre + " ya está registrada");
		}

		categoria.updateDetails(nombre, comando.activo(), LocalDateTime.now(clock));

		MenuCategory guardada = menucategoryrepository.update(categoria);
		return CategoryResult.convertirA(guardada);
	}

}
