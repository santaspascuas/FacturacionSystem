package com.facturacionAutonomo.documentacionfactura_service.application.result;

import com.facturacionAutonomo.documentacionfactura_service.domain.model.Issuer;

/**
 * Datos fiscales del restaurante (el emisor del ticket) tal como salen por la API de este servicio.
 * Se construye desde el dominio (Issuer), no desde el JSON de customer-service: el dominio nunca sale por la API.
 */
public class CustomerResult {
	private final String razonSocial;
	private final String nif;
	private final Address direccion;                 // null si no tiene dirección fiscal
	private final boolean datosFiscalesCompletos;    // si se podrán emitir tickets (razón social y NIF)

	private CustomerResult(String razonSocial, String nif, Address direccion, boolean datosFiscalesCompletos) {
		this.razonSocial = razonSocial;
		this.nif = nif;
		this.direccion = direccion;
		this.datosFiscalesCompletos = datosFiscalesCompletos;
	}

	/** datosFiscalesCompletos sale de Issuer.tieneDatosFiscales(): la regla vive en el dominio, el result la copia. */
	public static CustomerResult from(Issuer emisor) {
		return new CustomerResult(emisor.getRazonSocial(), emisor.getNif(),
				emisor.getDireccion() == null ? null : Address.from(emisor.getDireccion()),
				emisor.tieneDatosFiscales());
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public String getNif() {
		return nif;
	}

	public Address getDireccion() {
		return direccion;
	}

	/** Jackson lo saca como "datosFiscalesCompletos" (los getters de boolean empiezan por is). */
	public boolean isDatosFiscalesCompletos() {
		return datosFiscalesCompletos;
	}

	@Override
	public String toString() {
		return "CustomerResult [razonSocial=" + razonSocial + ", nif=" + nif + ", direccion=" + direccion
				+ ", datosFiscalesCompletos=" + datosFiscalesCompletos + "]";
	}
}
