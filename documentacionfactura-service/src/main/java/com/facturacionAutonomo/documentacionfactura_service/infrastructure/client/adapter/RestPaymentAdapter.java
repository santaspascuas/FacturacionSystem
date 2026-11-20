package com.facturacionAutonomo.documentacionfactura_service.infrastructure.client.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import com.facturacionAutonomo.documentacionfactura_service.application.port.out.PaymentPort;
import com.facturacionAutonomo.documentacionfactura_service.domain.model.TicketPayment;
import com.facturacionAutonomo.documentacionfactura_service.infrastructure.client.dto.PaymentResponse;
import com.facturacionAutonomo.documentacionfactura_service.infrastructure.client.dto.RemoteApiResponse;
import com.facturacionAutonomo.documentacionfactura_service.infrastructure.client.mapper.PaymentResponseMapper;

/** PaymentPort sobre HTTP: GET /api/order/{publicId}/payments de product-service. */
@Component
public class RestPaymentAdapter implements PaymentPort {

	private static final Logger log = LoggerFactory.getLogger(RestPaymentAdapter.class);

	// data es un array JSON: el tipo tiene que ser una lista, no un PaymentResponse suelto.
	private static final ParameterizedTypeReference<RemoteApiResponse<List<PaymentResponse>>> TIPO_LISTA_PAGOS =
			new ParameterizedTypeReference<>() {};

	private final ProductServiceCaller productService;

	public RestPaymentAdapter(ProductServiceCaller productService) {
		this.productService = productService;
	}

	@Override
	public Optional<List<TicketPayment>> listarPagos(UUID tenantId, UUID orderPublicId) {
		log.debug("[product-service] Consultando cobros del pedido {} del tenant {}", orderPublicId, tenantId);
		return productService.get(tenantId, TIPO_LISTA_PAGOS, "/api/order/{publicId}/payments", orderPublicId)
				.map(PaymentResponseMapper::toDomain);
	}
}
