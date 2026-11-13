package com.facturacionAutonomo.product_service.application.service;

import java.time.Clock;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.product_service.application.command.tables.UpdateTablesCommand;
import com.facturacionAutonomo.product_service.application.port.OUT.TablesRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.in.tables.UpdateTablesUseCase;
import com.facturacionAutonomo.product_service.application.result.TablesResult;
import com.facturacionAutonomo.product_service.application.service.finder.TablesItemFinder;
import com.facturacionAutonomo.product_service.domain.model.TableRestautant;

@Service
public class UpdateTableMenuService implements UpdateTablesUseCase{

	private static final Logger log = LoggerFactory.getLogger(UpdateTableMenuService.class);
	private final Clock clock;
	private final TablesRepositoryPort tablerespository;
	private final TablesItemFinder finder;


	public UpdateTableMenuService(Clock clock, TablesRepositoryPort tablerespository, TablesItemFinder finder) {
		super();
		this.clock = clock;
		this.tablerespository = tablerespository;
		this.finder = finder;
	}


	@Override
	@Transactional
	public TablesResult execute(UpdateTablesCommand commando) {

		finder.tenantObligatorio(commando.tenantId());
		log.info("[UpdateTableMenuService] modificación de mesa tenantId: {}", commando.tenantId());

		int numero = TableRestautant.numeroValido(commando.tableNumber());

		//buscamos la mesa
		TableRestautant mesa = finder.encuentraTenatandPublic(commando.tenantId(), commando.publicId());

		//si cambia el numero comprobamos que esta libre; si no cambia, la mesa se encontraria a si misma (409 falso)
		if(numero != mesa.getTableNumber()) {
			finder.numeroLibre(commando.tenantId(), numero);
		}

		//solo los datos de la mesa: numero, capacidad y zona. El estado lo cambia CambiarEstadoTableMenuService.
		mesa.updateDetails(numero, commando.capacity(), commando.zone(), LocalDateTime.now(clock));

		//update y no save: save haria un INSERT de una mesa nueva
		return TablesResult.convertirA(tablerespository.update(mesa));
	}

}
