package com.facturacionAutonomo.documentacionfactura_service.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.facturacionAutonomo.documentacionfactura_service.application.port.in.GetOrderUseCase;
import com.facturacionAutonomo.documentacionfactura_service.application.port.out.OrderPort;
import com.facturacionAutonomo.documentacionfactura_service.application.query.GetOrderQuery;
import com.facturacionAutonomo.documentacionfactura_service.application.result.OrderResult;
import com.facturacionAutonomo.documentacionfactura_service.domain.exception.DatoInvalidoException;
import com.facturacionAutonomo.documentacionfactura_service.domain.exception.PedidoNoEncontradoException;
import com.facturacionAutonomo.documentacionfactura_service.domain.model.Order;

@Service
public class GetOrderService implements GetOrderUseCase{
	private static final Logger log = LoggerFactory.getLogger(GetOrderService.class);
	
	private final OrderPort orderport;
	
	

	public GetOrderService(OrderPort orderport) {
		super();
		this.orderport = orderport;
	}



	@Override
	public OrderResult execute(GetOrderQuery query) {
		if (query.tenantId() == null) {
			throw new DatoInvalidoException("El tenant es obligatorio");
		}
		if (query.orderPublicId() == null) {
			throw new DatoInvalidoException("El pedido es obligatorio");
		}
		log.info("[GetOrderService] pedido {} del tenant {}", query.orderPublicId(), query.tenantId());
		
		Order pedido = orderport.buscarPorPublicId(query.tenantId(), query.orderPublicId()).orElseThrow(
				()->new PedidoNoEncontradoException("El pedido no existe para este restaurante"));
		
		return OrderResult.from(pedido);
	}

}
