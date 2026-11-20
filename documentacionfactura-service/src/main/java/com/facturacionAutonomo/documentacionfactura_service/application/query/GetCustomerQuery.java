package com.facturacionAutonomo.documentacionfactura_service.application.query;

import java.util.UUID;

/** Consulta de los datos fiscales del restaurante (el emisor). Query y no command: solo lee. */
public record GetCustomerQuery(UUID tenantId) {
}
