package com.facturacionAutonomo.documentacionfactura_service.domain.exception;

/** El pedido no está pagado del todo (no está en CIERRAYPAGA o le queda importe pendiente): no se imprime. */
public class PedidoNoPagadoException extends RuntimeException {
	public PedidoNoPagadoException(String message) {
		super(message);
	}
}
