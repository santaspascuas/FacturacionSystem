package com.facturacionAutonomo.documentacionfactura_service.domain.exception;

/**
 * product-service no responde, responde con un error o devuelve algo que no se puede interpretar. El fallo no está en la petición que recibimos: se reintenta cuando el otro servicio vuelva.
 */
public class ServicioExternoException extends RuntimeException {
	public ServicioExternoException(String message) {
		super(message);
	}

	public ServicioExternoException(String message, Throwable cause) {
		super(message, cause);
	}
}
