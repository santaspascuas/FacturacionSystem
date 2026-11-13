package com.facturacionAutonomo.product_service.domain.exception;

// "Una mesa, una orden": la mesa ya tiene un pedido abierto (responde 409).
public class MesaConPedidoAbiertoException extends RuntimeException{
	public MesaConPedidoAbiertoException(String message) {
		super(message);
	}

}
