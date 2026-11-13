package com.facturacionAutonomo.product_service.domain.exception;

// La mesa tiene pedidos (historial de facturación) y no se puede eliminar (responde 409).
public class TableConPedidosException extends RuntimeException{
	public TableConPedidosException(String message) {
		super(message);
	}

}
