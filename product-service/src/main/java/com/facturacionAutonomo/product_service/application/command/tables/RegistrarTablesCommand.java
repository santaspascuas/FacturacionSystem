package com.facturacionAutonomo.product_service.application.command.tables;

import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.TableZone;

// Sin publicId ni status: el publicId lo genera el dominio y una mesa nueva siempre nace DISPONIBLE.
// Integer y no int: si falta un dato llega null y el dominio lo rechaza con su mensaje, en vez de un 0 silencioso.
public record RegistrarTablesCommand(
		UUID tenantId,
		Integer tableNumber,
		Integer capacity,
		TableZone zone) {

}
