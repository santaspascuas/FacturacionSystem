package com.facturacionAutonomo.product_service.application.service;

import java.time.Clock;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.product_service.application.command.tables.RegistrarTablesCommand;
import com.facturacionAutonomo.product_service.application.port.OUT.TablesRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.in.tables.RegistrarTablesUseCase;
import com.facturacionAutonomo.product_service.application.result.TablesResult;
import com.facturacionAutonomo.product_service.application.service.finder.TablesItemFinder;
import com.facturacionAutonomo.product_service.domain.model.TableRestautant;

@Service
public class RegistrarTableMenuService implements RegistrarTablesUseCase{

	private static final Logger log = LoggerFactory.getLogger(RegistrarTableMenuService.class);
	private final Clock clock;
	private final TablesRepositoryPort tablerespository;
	private final TablesItemFinder finder;


	public RegistrarTableMenuService(Clock clock, TablesRepositoryPort tablerespository, TablesItemFinder finder) {
		super();
		this.clock = clock;
		this.tablerespository = tablerespository;
		this.finder = finder;
	}


	@Override
	@Transactional
	public TablesResult execute(RegistrarTablesCommand command) {

		finder.tenantObligatorio(command.tenantId());
		log.info("[RegistrarTableMenuService] alta de mesa tenantId: {}", command.tenantId());

		// Primero se valida el número (sin tocar la BD) y luego se comprueba que está libre en este tenant.
		int numero = TableRestautant.numeroValido(command.tableNumber());
		finder.numeroLibre(command.tenantId(), numero);

		// crearTable valida capacidad y zona, genera el publicId y deja la mesa DISPONIBLE.
		TableRestautant mesa = TableRestautant.crearTable(command.tenantId(), numero, command.capacity(),
				command.zone(), LocalDateTime.now(clock));

		return TablesResult.convertirA(tablerespository.save(mesa));
	}

}
