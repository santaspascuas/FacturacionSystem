package com.facturacionAutonomo.documentacionfactura_service.application.port.out;

import java.util.Optional;
import java.util.UUID;

import com.facturacionAutonomo.documentacionfactura_service.domain.model.Order;

/**
 * Puerto de salida hacia los pedidos de product-service.
 *
 * La firma no sabe nada del transporte: hoy lo implementa un adaptador HTTP (RestClient) y, cuando llegue RabbitMQ,
 * el listener llamará al mismo caso de uso, que seguirá pidiendo el pedido por aquí.
 */
public interface OrderPort {

	/**
	 * Pedido con sus líneas y sus cobros (en el orden en que se cobraron).
	 * Vacío si el pedido no existe para ese tenant: qué hacer entonces lo decide el caso de uso.
	 */
	Optional<Order> buscarPorPublicId(UUID tenantId, UUID orderPublicId);
	
}
