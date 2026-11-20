package com.facturacionAutonomo.documentacionfactura_service.infrastructure.client.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.facturacionAutonomo.documentacionfactura_service.infrastructure.client.dto.RemoteApiResponse;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

/**
 * Llamadas GET a customer-service (RestCustomerAdapter). La lógica HTTP está en RemoteServiceCaller; aquí solo va el
 * circuit breaker de customer-service (resilience4j.circuitbreaker.instances.customer-service en application.yml),
 * independiente del de product-service: si se cae uno, el otro sigue funcionando.
 */
@Component
public class CustomerServiceCaller extends RemoteServiceCaller {

	/** Nombre de la instancia de circuit breaker en application.yml. */
	static final String CIRCUIT_BREAKER = "customer-service";

	public CustomerServiceCaller(@Qualifier("customerServiceRestClient") RestClient customerServiceRestClient) {
		super(customerServiceRestClient, CIRCUIT_BREAKER);
	}

	@CircuitBreaker(name = CIRCUIT_BREAKER, fallbackMethod = "noDisponible")
	public <T> Optional<T> get(UUID tenantId, ParameterizedTypeReference<RemoteApiResponse<T>> tipo, String uri,
			Object... uriVariables) {
		return llamar(tenantId, tipo, uri, uriVariables);
	}

	/** Fallback con el circuito abierto: como el de ProductServiceCaller. */
	@SuppressWarnings("unused") // lo invoca Resilience4j por reflexión
	private <T> Optional<T> noDisponible(UUID tenantId, ParameterizedTypeReference<RemoteApiResponse<T>> tipo,
			String uri, Object[] uriVariables, CallNotPermittedException ex) {
		throw circuitoAbierto(uri, ex);
	}
}
