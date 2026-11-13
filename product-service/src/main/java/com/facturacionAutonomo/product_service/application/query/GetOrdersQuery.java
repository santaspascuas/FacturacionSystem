package com.facturacionAutonomo.product_service.application.query;

import java.util.UUID;

// soloActivas = false: todos los pedidos (del más reciente al más antiguo).
// soloActivas = true: solo los abiertos (del más antiguo al más nuevo, como una cola).
public record GetOrdersQuery(
		UUID tenantId,
		boolean soloActivas
		) {

}
