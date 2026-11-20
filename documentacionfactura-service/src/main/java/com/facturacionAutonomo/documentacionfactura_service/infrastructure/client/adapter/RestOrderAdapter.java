package com.facturacionAutonomo.documentacionfactura_service.infrastructure.client.adapter;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import com.facturacionAutonomo.documentacionfactura_service.application.port.out.OrderPort;
import com.facturacionAutonomo.documentacionfactura_service.application.port.out.PaymentPort;
import com.facturacionAutonomo.documentacionfactura_service.domain.model.Order;
import com.facturacionAutonomo.documentacionfactura_service.infrastructure.client.dto.OrderResponse;
import com.facturacionAutonomo.documentacionfactura_service.infrastructure.client.dto.RemoteApiResponse;
import com.facturacionAutonomo.documentacionfactura_service.infrastructure.client.mapper.OrderResponseMapper;

/**
 * OrderPort sobre HTTP: GET /api/order/{publicId} de product-service. Los cobros los trae PaymentPort, así que el
 * pedido sale completo (con líneas y cobros) y el caso de uso no tiene que saber que product-service los da aparte.
 */
@Component
public class RestOrderAdapter implements OrderPort {

	private static final Logger log = LoggerFactory.getLogger(RestOrderAdapter.class);

	// product-service envuelve el pedido en { success, message, data, errorCode }. Por el borrado de genéricos,
	// RemoteApiResponse.class no le diría a Jackson qué hay en data: la subclase anónima ({}) guarda el tipo completo.
	private static final ParameterizedTypeReference<RemoteApiResponse<OrderResponse>> TIPO_PEDIDO =
			new ParameterizedTypeReference<>() {};

	private final ProductServiceCaller productService;
	private final PaymentPort paymentPort;

	public RestOrderAdapter(ProductServiceCaller productService, PaymentPort paymentPort) {
		this.productService = productService;
		this.paymentPort = paymentPort;
	}

	@Override
	public Optional<Order> buscarPorPublicId(UUID tenantId, UUID orderPublicId) {
		log.debug("[product-service] Consultando pedido {} del tenant {}", orderPublicId, tenantId);

		Optional<OrderResponse> pedido = productService.get(tenantId, TIPO_PEDIDO, "/api/order/{publicId}",
				orderPublicId);
		if (pedido.isEmpty()) {
			log.debug("[product-service] El pedido {} no existe para el tenant {}", orderPublicId, tenantId);
			return Optional.empty();
		}

		// Si los cobros dan vacío, el pedido desapareció entre las dos llamadas: se trata como inexistente.
		return paymentPort.listarPagos(tenantId, orderPublicId)
				.map(cobros -> OrderResponseMapper.toDomain(pedido.get(), cobros));
	}
}
