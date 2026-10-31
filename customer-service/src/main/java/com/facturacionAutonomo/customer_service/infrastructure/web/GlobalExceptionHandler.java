package com.facturacionAutonomo.customer_service.infrastructure.web;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.facturacionAutonomo.customer_service.domain.exception.AddressNoEncontradoException;
import com.facturacionAutonomo.customer_service.domain.exception.CustomerNoEncontradoException;
import com.facturacionAutonomo.customer_service.domain.exception.CustomerYaRegistradoException;
import com.facturacionAutonomo.customer_service.domain.exception.DatoInvalidoException;
import com.facturacionAutonomo.customer_service.domain.exception.EmailYaRegistradoException;
import com.facturacionAutonomo.customer_service.infrastructure.utilitys.ApiResponse;
import com.facturacionAutonomo.customer_service.infrastructure.utilitys.ResponseUtil;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	// Excepciones de negocio: cada una con su código HTTP.
	// Al crear una excepción nueva, añádela aquí en el mismo cambio: si no, acabará en handleInesperada (500).

	@ExceptionHandler(DatoInvalidoException.class)
	public ResponseEntity<ApiResponse<Object>> handleDatoInvalido(DatoInvalidoException ex) {
		log.debug("[handler] {} -> 400", ex.getClass().getSimpleName());
		return respuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler({ CustomerNoEncontradoException.class, AddressNoEncontradoException.class })
	public ResponseEntity<ApiResponse<Object>> handleNoEncontrado(RuntimeException ex) {
		log.debug("[handler] {} -> 404", ex.getClass().getSimpleName());
		return respuesta(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler({ CustomerYaRegistradoException.class, EmailYaRegistradoException.class })
	public ResponseEntity<ApiResponse<Object>> handleYaRegistrado(RuntimeException ex) {
		log.debug("[handler] {} -> 409", ex.getClass().getSimpleName());
		return respuesta(HttpStatus.CONFLICT, ex.getMessage());
	}

	// @Version: otra petición modificó el customer entre nuestra lectura y nuestro UPDATE.
	@ExceptionHandler(OptimisticLockingFailureException.class)
	public ResponseEntity<ApiResponse<Object>> handleConcurrencia(OptimisticLockingFailureException ex) {
		log.debug("[handler] Conflicto de concurrencia: {}", ex.getMessage());
		return respuesta(HttpStatus.CONFLICT, "Los datos han sido modificados por otra petición. Vuelve a intentarlo");
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Object>> handleValidacion(MethodArgumentNotValidException ex) {
		String mensaje = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getDefaultMessage())
				.collect(Collectors.joining("; "));
		log.debug("[handler] Validacion fallida: {}", mensaje);
		return respuesta(HttpStatus.BAD_REQUEST, mensaje);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiResponse<Object>> handleCuerpoIlegible(HttpMessageNotReadableException ex) {
		log.debug("[handler] Cuerpo de la peticion ilegible");
		return respuesta(HttpStatus.BAD_REQUEST, "El cuerpo de la petición no es un JSON válido");
	}

	@ExceptionHandler(MissingRequestHeaderException.class)
	public ResponseEntity<ApiResponse<Object>> handleCabeceraAusente(MissingRequestHeaderException ex) {
		log.debug("[handler] Cabecera ausente: {}", ex.getHeaderName());
		return respuesta(HttpStatus.BAD_REQUEST, "La cabecera " + ex.getHeaderName() + " es obligatoria");
	}

	// Sirve para cabeceras y rutas: getName() dice cuál falló (X-Tenant-Id, publicId...).
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiResponse<Object>> handleTipoInvalido(MethodArgumentTypeMismatchException ex) {
		log.debug("[handler] Formato invalido en: {}", ex.getName());
		return respuesta(HttpStatus.BAD_REQUEST, "El valor de " + ex.getName() + " no tiene un formato válido");
	}

	// Dos altas simultáneas pasan la comprobación previa y la segunda choca con el UNIQUE de la BD.
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiResponse<Object>> handleIntegridad(DataIntegrityViolationException ex) {
		log.debug("[handler] Violacion de integridad: {}", ex.getMostSpecificCause().getMessage());
		return respuesta(HttpStatus.CONFLICT, "El customer o su email ya están registrados");
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Object>> handleInesperada(Exception ex) {
		log.error("[handler] Error inesperado", ex);
		return respuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
	}

	private static ResponseEntity<ApiResponse<Object>> respuesta(HttpStatus status, String mensaje) {
		return ResponseEntity.status(status).body(ResponseUtil.error(mensaje, status.value()));
	}
}
