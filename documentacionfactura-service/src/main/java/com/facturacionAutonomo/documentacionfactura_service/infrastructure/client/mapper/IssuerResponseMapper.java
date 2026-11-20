package com.facturacionAutonomo.documentacionfactura_service.infrastructure.client.mapper;

import java.util.List;
import java.util.Objects;

import com.facturacionAutonomo.documentacionfactura_service.domain.model.FiscalAddress;
import com.facturacionAutonomo.documentacionfactura_service.domain.model.Issuer;
import com.facturacionAutonomo.documentacionfactura_service.infrastructure.client.dto.AddressResponse;
import com.facturacionAutonomo.documentacionfactura_service.infrastructure.client.dto.CustomerResponse;

/**
 * JSON de customer-service → emisor del ticket. Solo traduce: si los datos fiscales están completos lo decide el
 * dominio (Issuer.tieneDatosFiscales, Ticket.validarEmision).
 */
public final class IssuerResponseMapper {

	private IssuerResponseMapper() {
	}

	public static Issuer toDomain(CustomerResponse customer) {
		return Issuer.restoreData(customer.razonSocial(), customer.nif(), direccionFiscal(customer.addresses()));
	}

	// La fiscal es la que tiene active = true. Sin ninguna, el ticket sale sin domicilio (Issuer admite null).
	private static FiscalAddress direccionFiscal(List<AddressResponse> direcciones) {
		if (direcciones == null) {
			return null;
		}
		return direcciones.stream()
				.filter(Objects::nonNull)
				.filter(AddressResponse::active)
				.findFirst()
				.map(d -> FiscalAddress.restoreData(d.calle(), d.postalCode(), d.city(), d.province(), d.country()))
				.orElse(null);
	}
}
