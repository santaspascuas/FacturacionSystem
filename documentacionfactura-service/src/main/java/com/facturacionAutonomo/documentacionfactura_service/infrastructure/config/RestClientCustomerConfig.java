package com.facturacionAutonomo.documentacionfactura_service.infrastructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Conexión con customer-service: su RestClient, construido con RestClientFactory.
 * El aviso de los cambios de estado de su circuit breaker ya lo da el bean de RestClientConfig, que vale para todos.
 */
@Configuration
@EnableConfigurationProperties(CustomerServiceProperties.class)
public class RestClientCustomerConfig {

	// El nombre del método es el nombre del bean: tiene que ser distinto de productServiceRestClient.
	@Bean
	RestClient customerServiceRestClient(CustomerServiceProperties properties) {
		return RestClientFactory.crear(properties.baseUrl(), properties.connectTimeout(), properties.readTimeout());
	}
}
