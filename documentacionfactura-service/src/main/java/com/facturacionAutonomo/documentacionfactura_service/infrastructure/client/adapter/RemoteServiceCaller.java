package com.facturacionAutonomo.documentacionfactura_service.infrastructure.client.adapter;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.facturacionAutonomo.documentacionfactura_service.domain.exception.ServicioExternoException;
import com.facturacionAutonomo.documentacionfactura_service.infrastructure.client.dto.RemoteApiResponse;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;

/**
 * Lógica común de las llamadas GET a otros servicios (ProductServiceCaller y CustomerServiceCaller), para no repetirla
 * en cada caller: cabecera del tenant, comprobación del sobre { success, data } y traducción de errores.
 *
 * El circuit breaker NO va aquí: @CircuitBreaker necesita un nombre fijo, así que cada subclase lo pone en su propio
 * método público get, que se limita a llamar a llamar(...). Así cada servicio tiene su circuito independiente.
 */
public abstract class RemoteServiceCaller {

	// getClass(): los logs salen con el nombre del caller concreto (ProductServiceCaller, CustomerServiceCaller).
	private final Logger log = LoggerFactory.getLogger(getClass());

	private final RestClient restClient;
	private final String servicio;   // "product-service" / "customer-service": para los logs y los mensajes de error

	protected RemoteServiceCaller(RestClient restClient, String servicio) {
		this.restClient = restClient;
		this.servicio = servicio;
	}

	/**
	 * Devuelve el data del sobre { success, message, data, errorCode }.
	 * Vacío si el otro servicio responde 404 (no existe para ese tenant). Sin conexión, tiempo agotado, otro código de
	 * error, un JSON ilegible o un sobre sin datos: ServicioExternoException (la API responde 502), que es lo que el
	 * circuit breaker cuenta como fallo. Un 404 devuelve Optional.empty(): no es un fallo.
	 */
	protected <T> Optional<T> llamar(UUID tenantId, ParameterizedTypeReference<RemoteApiResponse<T>> tipo, String uri,
			Object... uriVariables) {
		try {
			RemoteApiResponse<T> respuesta = restClient.get()
					.uri(uri, uriVariables)
					.header("X-Tenant-Id", tenantId.toString()) // toda consulta de un tenant lleva su id (regla del monorepo)
					.retrieve()
					.body(tipo);

			if (respuesta == null || !respuesta.success() || respuesta.data() == null) {
				throw new ServicioExternoException(servicio + " devolvió una respuesta sin datos en " + uri);
			}
			return Optional.of(respuesta.data());

		} catch (HttpClientErrorException.NotFound e) {
			log.debug("[{}] 404 en {}", servicio, uri);
			return Optional.empty();
		} catch (ResourceAccessException e) {
			// Sin conexión o tiempo agotado.
			log.warn("[{}] Sin respuesta en {}", servicio, uri, e);
			throw new ServicioExternoException(servicio + " no responde", e);
		} catch (RestClientException e) {
			// 5xx, otros 4xx o JSON ilegible (por ejemplo, un enum que no conocemos).
			log.warn("[{}] Error en {}", servicio, uri, e);
			throw new ServicioExternoException(servicio + " ha fallado al responder a " + uri, e);
		}
	}

	/**
	 * Lo que lanzan los fallbacks de las subclases con el circuito abierto: no se ha llamado a la red. No inventa datos:
	 * devolver Optional.empty() sería decir "no existe" cuando lo que pasa es que el servicio está caído.
	 */
	protected ServicioExternoException circuitoAbierto(String uri, CallNotPermittedException ex) {
		log.debug("[{}] Circuito abierto: no se llama a {}", servicio, uri);
		return new ServicioExternoException(servicio + " no está disponible; inténtalo de nuevo en unos segundos", ex);
	}
}
