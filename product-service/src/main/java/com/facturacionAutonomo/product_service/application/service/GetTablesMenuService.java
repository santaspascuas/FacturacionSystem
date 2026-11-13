package com.facturacionAutonomo.product_service.application.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.product_service.application.port.OUT.TablesRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.in.tables.GetTablesUseCase;
import com.facturacionAutonomo.product_service.application.query.GetTablesQuery;
import com.facturacionAutonomo.product_service.application.result.TablesResult;
import com.facturacionAutonomo.product_service.application.service.finder.TablesItemFinder;
import com.facturacionAutonomo.product_service.domain.model.TableRestautant;

@Service
public class GetTablesMenuService implements GetTablesUseCase{

	private static final Logger log = LoggerFactory.getLogger(GetTablesMenuService.class);
	private final TablesRepositoryPort tablerespository;
	private final TablesItemFinder finder;


	public GetTablesMenuService(TablesRepositoryPort tablerespository, TablesItemFinder finder) {
		super();
		this.tablerespository = tablerespository;
		this.finder = finder;
	}


	@Override
	@Transactional(readOnly = true)
	public List<TablesResult> execute(GetTablesQuery query) {

		finder.tenantObligatorio(query.tenantId());
		log.info("[GetTablesMenuService] listado de mesas tenantId: {}", query.tenantId());

		return buscarMesas(query).stream().map(TablesResult::convertirA).toList();
	}


	// Sin estado se listan todas las mesas del tenant; con estado (p. ej. DISPONIBLE), solo las que lo tienen.
	private List<TableRestautant> buscarMesas(GetTablesQuery query) {
		if(query.status() == null) {
			return tablerespository.findByTenantId(query.tenantId());
		}
		return tablerespository.findByTenantIdAndStatus(query.tenantId(), query.status());
	}

}
