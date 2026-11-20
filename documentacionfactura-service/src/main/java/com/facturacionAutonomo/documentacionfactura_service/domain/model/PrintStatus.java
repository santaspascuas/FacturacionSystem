package com.facturacionAutonomo.documentacionfactura_service.domain.model;

/**
 * Resultado de la última impresión de un ticket.
 * PENDIENTE: emitido y guardado, todavía sin mandar a la impresora.
 * IMPRESO: la impresora lo aceptó. ERROR: falló; el ticket existe y se puede reimprimir.
 */
public enum PrintStatus {
	PENDIENTE,
	IMPRESO,
	ERROR
}
