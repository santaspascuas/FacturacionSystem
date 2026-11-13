package com.facturacionAutonomo.product_service.application.service;

import java.time.Clock;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.product_service.application.command.category.RegistrarCategoryCommand;
import com.facturacionAutonomo.product_service.application.port.OUT.MenuCategoryRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.in.RegistarCategoryUseCase;
import com.facturacionAutonomo.product_service.application.result.CategoryResult;
import com.facturacionAutonomo.product_service.domain.exception.CategoryYaRegistradoException;
import com.facturacionAutonomo.product_service.domain.model.MenuCategory;

@Service
public class RegisterCategoryService implements RegistarCategoryUseCase{

	private static final Logger log = LoggerFactory.getLogger(RegisterCategoryService.class);

	private final MenuCategoryRepositoryPort menucategoryrepsoitiory;
	private final CategoryFinder finder;
	private final Clock clock;


	public RegisterCategoryService(MenuCategoryRepositoryPort menucategoryrepsoitiory, CategoryFinder finder,
			Clock clock) {
		super();
		this.menucategoryrepsoitiory = menucategoryrepsoitiory;
		this.finder = finder;
		this.clock = clock;
	}


	@Override
	@Transactional
	public CategoryResult execute(RegistrarCategoryCommand commando) {

		finder.tenantObligatorio(commando.tenantId());
		log.info("[RegisterCategoryService] alta de categoria tenantId: {}", commando.tenantId());

		String name = MenuCategory.normalizarNombre(commando.name());

		//no metemos la misma categoria dos veces en el mismo tenant
		if(menucategoryrepsoitiory.existsByTenantIdAndName(commando.tenantId(), name)) {
			throw new CategoryYaRegistradoException("La categoría " + name + " ya está registrada");
		}

		// La categoría nueva va al final de la carta del tenant.
		int displayOrder = menucategoryrepsoitiory.findMaxDisplayOrderByTenantId(commando.tenantId()) + 1;

		MenuCategory categoria = MenuCategory.crearCategory(commando.tenantId(), name, displayOrder,
				LocalDateTime.now(clock));

		MenuCategory guardada = menucategoryrepsoitiory.save(categoria);
		return CategoryResult.convertirA(guardada);
	}

}
