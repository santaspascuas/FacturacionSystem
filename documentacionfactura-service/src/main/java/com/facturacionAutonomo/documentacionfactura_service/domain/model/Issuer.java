package com.facturacionAutonomo.documentacionfactura_service.domain.model;

/**
 * Emisor del ticket: el restaurante (el customer de customer-service). Inmutable.
 * Razón social y NIF son obligatorios en una factura simplificada; la dirección es opcional (puede ser null).
 */
public class Issuer {
	private final String razonSocial;
	private final String nif;
	private final FiscalAddress direccion;

	private Issuer(String razonSocial, String nif, FiscalAddress direccion) {
		this.razonSocial = razonSocial;
		this.nif = nif;
		this.direccion = direccion;
	}

	/** No valida: si faltan datos fiscales lo decide Ticket.validarEmision, con su propia excepción. */
	public static Issuer restoreData(String razonSocial, String nif, FiscalAddress direccion) {
		return new Issuer(razonSocial, nif, direccion);
	}

	public boolean tieneDatosFiscales() {
		return razonSocial != null && !razonSocial.isBlank() && nif != null && !nif.isBlank();
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public String getNif() {
		return nif;
	}

	public FiscalAddress getDireccion() {
		return direccion;
	}

	@Override
	public String toString() {
		return "Issuer [razonSocial=" + razonSocial + ", nif=" + nif + ", direccion=" + direccion + "]";
	}
}
