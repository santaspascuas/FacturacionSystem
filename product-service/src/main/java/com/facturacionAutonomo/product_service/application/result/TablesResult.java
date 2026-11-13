package com.facturacionAutonomo.product_service.application.result;

import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.TableRestautant;
import com.facturacionAutonomo.product_service.domain.model.TableStatus;
import com.facturacionAutonomo.product_service.domain.model.TableZone;

// Lo que sale hacia fuera de una mesa. Lo devuelven el alta, la modificación y el listado, como MenuItemResult.
// Ni el id interno ni el tenant salen: el cliente identifica la mesa por su publicId.
public record TablesResult(
		UUID publicId,
		int tableNumber,
		int capacity,
		TableZone zone,
		TableStatus status
		) {

	public static TablesResult convertirA(TableRestautant mesa) {
		return new TablesResult(mesa.getPublicId(), mesa.getTableNumber(), mesa.getCapacity(), mesa.getZone(),
				mesa.getStatus());
	}

}
