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
 * Llamadas GET a product-service, compartidas por RestOrderAdapter y RestPaymentAdapter. La lógica HTTP está en
 * RemoteServiceCaller; aquí solo va el circuit breaker de product-service (resilience4j.circuitbreaker.instances
 * .product-service en application.yml).
 *
 * La anotación funciona porque Spring envuelve este bean en un proxy (AOP) y los adaptadores llaman a get desde fuera
 * de la clase. Una llamada desde dentro de esta misma clase no pasaría por el proxy.
 */
@Component
public class ProductServiceCaller extends RemoteServiceCaller {

	/** Nombre de la instancia de circuit breaker en application.yml. */
	static final String CIRCUIT_BREAKER = "product-service";

	// Hay dos RestClient (product-service y customer-service): @Qualifier dice cuál inyectar, por el nombre del bean.
	public ProductServiceCaller(@Qualifier("productServiceRestClient") RestClient productServiceRestClient) {
		super(productServiceRestClient, CIRCUIT_BREAKER);
	}

	@CircuitBreaker(name = CIRCUIT_BREAKER, fallbackMethod = "noDisponible")
	public <T> Optional<T> get(UUID tenantId, ParameterizedTypeReference<RemoteApiResponse<T>> tipo, String uri,
			Object... uriVariables) {
		return llamar(tenantId, tipo, uri, uriVariables);
	}

	/**
	 * Fallback del circuit breaker: mismos parámetros que get (los varargs se declaran como Object[]) más la excepción.
	 * Solo recibe CallNotPermittedException (circuito abierto); cualquier otra excepción no encaja y se relanza tal cual.
	 */
	@SuppressWarnings("unused") // lo invoca Resilience4j por reflexión
	private <T> Optional<T> noDisponible(UUID tenantId, ParameterizedTypeReference<RemoteApiResponse<T>> tipo,
			String uri, Object[] uriVariables, CallNotPermittedException ex) {
		throw circuitoAbierto(uri, ex);
	}
}
