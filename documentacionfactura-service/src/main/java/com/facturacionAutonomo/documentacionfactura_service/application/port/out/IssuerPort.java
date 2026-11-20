package com.facturacionAutonomo.documentacionfactura_service.application.port.out;

import java.util.Optional;
import java.util.UUID;

import com.facturacionAutonomo.documentacionfactura_service.domain.model.Issuer;

/**
 * Puerto de salida hacia los datos fiscales del restaurante (el emisor del ticket). Hoy lo implementa un adaptador
 * HTTP contra customer-service (GET /api/customer/infome); el caso de uso no sabe de dónde salen.
 */
public interface IssuerPort {

	/**
	 * Razón social, NIF y dirección fiscal (la que tiene active = true en customer-service; null si no hay ninguna).
	 * Vacío si el restaurante no está registrado (customer-service responde 404): qué hacer lo decide el caso de uso.
	 * No comprueba que los datos fiscales estén completos: esa regla vive en Issuer.tieneDatosFiscales() y
	 * Ticket.validarEmision.
	 */
	Optional<Issuer> buscarPorTenant(UUID tenantId);
}
