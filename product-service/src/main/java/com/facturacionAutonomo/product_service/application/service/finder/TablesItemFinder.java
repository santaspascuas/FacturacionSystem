package com.facturacionAutonomo.product_service.application.service.finder;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.facturacionAutonomo.product_service.application.port.OUT.TablesRepositoryPort;
import com.facturacionAutonomo.product_service.domain.exception.TableException;
import com.facturacionAutonomo.product_service.domain.exception.TableNotFoundException;
import com.facturacionAutonomo.product_service.domain.exception.TableYaRegistradaException;
import com.facturacionAutonomo.product_service.domain.model.TableRestautant;

/**
 * Búsquedas y comprobaciones que comparten los casos de uso de mesas. Todas filtran por tenant:
 * una mesa de otro restaurante, para este tenant no existe.
 */
@Component
public class TablesItemFinder {

	private final TablesRepositoryPort tablerespository;

	public TablesItemFinder(TablesRepositoryPort tablerespository) {
		super();
		this.tablerespository = tablerespository;
	}

	public void tenantObligatorio(UUID tenantId) {
		if(tenantId == null) {
			throw new TableException("El tenant es obligatorio");
		}
	}

	// El puerto devuelve un Optional: si está vacío (no existe o es de otro tenant) se responde 404.
	public TableRestautant encuentraTenatandPublic(UUID tenantId, UUID publicId) {
		if(publicId == null) {
			throw new TableException("El publicId es obligatorio");
		}
		return tablerespository.findByTenantIdAndPublicId(tenantId, publicId)
				.orElseThrow(() -> new TableNotFoundException("La mesa no se ha encontrado"));
	}

	// La usan el alta y, cuando exista, la modificación del número de mesa.
	public void numeroLibre(UUID tenantId, int tableNumber) {
		if(tablerespository.existsByTenantIdAndTableNumber(tenantId, tableNumber)) {
			throw new TableYaRegistradaException("La mesa " + tableNumber + " ya está registrada");
		}
	}

}
