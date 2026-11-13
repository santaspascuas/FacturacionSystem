package com.facturacionAutonomo.product_service.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.product_service.application.command.tables.EliminarTablesCommand;
import com.facturacionAutonomo.product_service.application.port.OUT.TablesRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.in.tables.EliminarTablesUseCase;
import com.facturacionAutonomo.product_service.application.service.finder.TablesItemFinder;
import com.facturacionAutonomo.product_service.domain.exception.TableConPedidosException;
import com.facturacionAutonomo.product_service.domain.model.TableRestautant;

@Service
public class EliminarTableMenuService implements EliminarTablesUseCase{

	private static final Logger log = LoggerFactory.getLogger(EliminarTableMenuService.class);
	private final TablesRepositoryPort tablerespository;
	private final TablesItemFinder finder;


	public EliminarTableMenuService(TablesRepositoryPort tablerespository, TablesItemFinder finder) {
		super();
		this.tablerespository = tablerespository;
		this.finder = finder;
	}


	@Override
	@Transactional
	public void execute(EliminarTablesCommand command) {

		finder.tenantObligatorio(command.tenantId());
		log.info("[EliminarTableMenuService] baja de mesa tenantId: {}", command.tenantId());

		// Se busca primero por tenant: así una mesa de otro restaurante da 404 y no se borra.
		TableRestautant mesa = finder.encuentraTenatandPublic(command.tenantId(), command.publicId());

		// Una mesa con pedidos (aunque estén cerrados) es historial de facturación: no se borra.
		if(tablerespository.tienePedidos(mesa.getId())) {
			throw new TableConPedidosException("La mesa " + mesa.getTableNumber() + " tiene pedidos y no se puede eliminar");
		}
		tablerespository.deleteById(mesa.getId());
	}

}
