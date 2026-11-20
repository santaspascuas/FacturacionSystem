package com.facturacionAutonomo.documentacionfactura_service.infrastructure.config;

import java.net.http.HttpClient;
import java.time.Duration;

import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * Construcción común de los RestClient de los servicios remotos (RestClientConfig y RestClientCustomerConfig), para
 * no repetir el mismo código en cada configuración.
 *
 * Se construye a mano con RestClient.builder(): el RestClient.Builder autoconfigurado vive en spring-boot-restclient,
 * que este proyecto no incluye. Los tiempos de espera son obligatorios: sin ellos, un servicio colgado dejaría la
 * petición del TPV esperando para siempre.
 */
final class RestClientFactory {

	private RestClientFactory() {
	}

	static RestClient crear(String baseUrl, Duration connectTimeout, Duration readTimeout) {
		HttpClient httpClient = HttpClient.newBuilder()
				.connectTimeout(connectTimeout)
				.build();
		JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
		requestFactory.setReadTimeout(readTimeout);

		return RestClient.builder()
				.baseUrl(baseUrl)
				.requestFactory(requestFactory)
				.defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
				.build();
	}
}
