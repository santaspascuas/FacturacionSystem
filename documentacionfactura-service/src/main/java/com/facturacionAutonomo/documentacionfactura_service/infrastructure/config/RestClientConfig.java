package com.facturacionAutonomo.documentacionfactura_service.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.core.registry.EntryRemovedEvent;
import io.github.resilience4j.core.registry.EntryReplacedEvent;
import io.github.resilience4j.core.registry.RegistryEventConsumer;

/**
 * Conexión con product-service (su RestClient, construido con RestClientFactory) y el aviso en el log de los cambios
 * de estado de los circuit breakers.
 */
@Configuration
@EnableConfigurationProperties(ProductServiceClientProperties.class)
public class RestClientConfig {

	private static final Logger log = LoggerFactory.getLogger(RestClientConfig.class);

	@Bean
	RestClient productServiceRestClient(ProductServiceClientProperties properties) {
		return RestClientFactory.crear(properties.baseUrl(), properties.connectTimeout(), properties.readTimeout());
	}

	/**
	 * Resilience4j crea los circuit breakers a partir del yml y avisa a los beans RegistryEventConsumer cuando añade
	 * uno. Aquí se aprovecha para escribir en el log cada cambio de estado (CLOSED_TO_OPEN, OPEN_TO_HALF_OPEN,
	 * HALF_OPEN_TO_CLOSED...): es lo que avisa de que product-service se ha caído o ha vuelto.
	 */
	@Bean
	RegistryEventConsumer<CircuitBreaker> circuitBreakerStateLogger() {
		return new RegistryEventConsumer<>() {
			@Override
			public void onEntryAddedEvent(EntryAddedEvent<CircuitBreaker> evento) {
				CircuitBreaker circuitBreaker = evento.getAddedEntry();
				circuitBreaker.getEventPublisher().onStateTransition(transicion ->
						log.warn("[{}] Circuit breaker: {}", circuitBreaker.getName(), transicion.getStateTransition()));
			}

			@Override
			public void onEntryRemovedEvent(EntryRemovedEvent<CircuitBreaker> evento) {
				// No se eliminan circuit breakers en tiempo de ejecución.
			}

			@Override
			public void onEntryReplacedEvent(EntryReplacedEvent<CircuitBreaker> evento) {
				// No se sustituyen circuit breakers en tiempo de ejecución.
			}
		};
	}
}
