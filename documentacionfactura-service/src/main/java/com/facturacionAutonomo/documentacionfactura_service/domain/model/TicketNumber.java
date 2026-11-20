package com.facturacionAutonomo.documentacionfactura_service.domain.model;

import com.facturacionAutonomo.documentacionfactura_service.domain.exception.DatoInvalidoException;

/**
 * Número fiscal del ticket: serie + año + número correlativo sin huecos dentro de cada serie y año. Inmutable.
 * Se imprime como "T2026-000001".
 */
public class TicketNumber {
	private final String serie;
	private final int anio;
	private final long numero;

	private TicketNumber(String serie, int anio, long numero) {
		this.serie = serie;
		this.anio = anio;
		this.numero = numero;
	}

	/** Número recién asignado por la numeración: se valida. */
	public static TicketNumber crearTicketNumber(String serie, int anio, long numero) {
		if (serie == null || serie.isBlank()) {
			throw new DatoInvalidoException("La serie del ticket es obligatoria");
		}
		if (numero <= 0) {
			throw new DatoInvalidoException("El número del ticket debe ser mayor que 0");
		}
		return new TicketNumber(serie, anio, numero);
	}

	/** Número ya emitido, leído de la BD: no se vuelve a validar. */
	public static TicketNumber restoreData(String serie, int anio, long numero) {
		return new TicketNumber(serie, anio, numero);
	}

	public String formateado() {
		return String.format("%s%d-%06d", serie, anio, numero);
	}

	public String getSerie() {
		return serie;
	}

	public int getAnio() {
		return anio;
	}

	public long getNumero() {
		return numero;
	}

	@Override
	public String toString() {
		return "TicketNumber [" + formateado() + "]";
	}
}
