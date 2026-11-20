package com.facturacionAutonomo.documentacionfactura_service.infrastructure.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Conexión con product-service, el único servicio al que se llama por HTTP (clients.product-service en
 * application.yml). @Validated: si falta un valor, el servicio no arranca, en vez de fallar en la primera petición.
 * El circuit breaker no está aquí: lo configura y lo lee Resilience4j (resilience4j.circuitbreaker en el yml).
 */
@Validated
@ConfigurationProperties(prefix = "clients.product-service")
public record ProductServiceClientProperties(
		@NotBlank String baseUrl,
		@NotNull Duration connectTimeout,
		@NotNull Duration readTimeout
		) {
}
