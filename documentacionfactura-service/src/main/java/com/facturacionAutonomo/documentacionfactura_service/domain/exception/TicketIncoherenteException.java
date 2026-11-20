package com.facturacionAutonomo.documentacionfactura_service.domain.exception;

/**
 * Los importes que llegan de product-service no cuadran (líneas, cobros y total no coinciden, o el pedido no
 * tiene líneas). Es un dato corrupto en origen: se rechaza antes de gastar un número fiscal.
 */
public class TicketIncoherenteException extends RuntimeException {
	public TicketIncoherenteException(String message) {
		super(message);
	}
}
