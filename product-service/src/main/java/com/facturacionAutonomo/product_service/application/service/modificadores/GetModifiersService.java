package com.facturacionAutonomo.product_service.application.service.modificadores;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.product_service.application.port.OUT.ModifierRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.in.GetModifiersUseCase;
import com.facturacionAutonomo.product_service.application.query.GetModifiersQuery;
import com.facturacionAutonomo.product_service.application.result.ModifierResult;
import com.facturacionAutonomo.product_service.application.service.finder.ModifierFinder;

@Service
public class GetModifiersService implements GetModifiersUseCase{

	private static final Logger log = LoggerFactory.getLogger(GetModifiersService.class);

	private final ModifierRepositoryPort modifierrepository;
	private final ModifierFinder finder;


	public GetModifiersService(ModifierRepositoryPort modifierrepository, ModifierFinder finder) {
		super();
		this.modifierrepository = modifierrepository;
		this.finder = finder;
	}


	@Override
	@Transactional(readOnly = true)
	public List<ModifierResult> execute(GetModifiersQuery query) {

		finder.tenantObligatorio(query.tenantId());
		log.info("[GetModifiersService] listado de modificadores tenantId: {}", query.tenantId());

		return modifierrepository.findActiveByTenantId(query.tenantId()).stream()
				.map(ModifierResult::convertirA).toList();
	}

}
