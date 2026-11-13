package com.facturacionAutonomo.product_service.application.service;

import java.time.Clock;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.product_service.application.command.tables.CambiarEstadoTablesCommand;
import com.facturacionAutonomo.product_service.application.port.OUT.TablesRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.in.tables.CambiarEstadoTablesUseCase;
import com.facturacionAutonomo.product_service.application.result.TablesResult;
import com.facturacionAutonomo.product_service.application.service.finder.TablesItemFinder;
import com.facturacionAutonomo.product_service.domain.model.TableRestautant;

@Service
public class CambiarEstadoTableMenuService implements CambiarEstadoTablesUseCase{

	private static final Logger log = LoggerFactory.getLogger(CambiarEstadoTableMenuService.class);
	private final Clock clock;
	private final TablesRepositoryPort tablerespository;
	private final TablesItemFinder finder;


	public CambiarEstadoTableMenuService(Clock clock, TablesRepositoryPort tablerespository, TablesItemFinder finder) {
		super();
		this.clock = clock;
		this.tablerespository = tablerespository;
		this.finder = finder;
	}


	// Solo el estado: no toca número, capacidad ni zona. Lo usan los camareros durante el servicio.
	@Override
	@Transactional
	public TablesResult execute(CambiarEstadoTablesCommand command) {

		finder.tenantObligatorio(command.tenantId());
		log.info("[CambiarEstadoTableMenuService] cambio de estado de mesa tenantId: {}", command.tenantId());

		TableRestautant mesa = finder.encuentraTenatandPublic(command.tenantId(), command.publicId());

		// cambiarEstado rechaza un estado null con TableException (400).
		mesa.cambiarEstado(command.status(), LocalDateTime.now(clock));

		return TablesResult.convertirA(tablerespository.update(mesa));
	}

}
