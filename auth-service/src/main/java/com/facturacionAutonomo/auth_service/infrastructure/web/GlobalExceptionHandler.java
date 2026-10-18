package com.facturacionAutonomo.auth_service.infrastructure.web;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.facturacionAutonomo.auth_service.domain.exception.CredencialesInvalidasException;
import com.facturacionAutonomo.auth_service.domain.exception.CuentaBloqueadaException;
import com.facturacionAutonomo.auth_service.domain.exception.CuentaExpiradaException;
import com.facturacionAutonomo.auth_service.domain.exception.DatoInvalidoException;
import com.facturacionAutonomo.auth_service.domain.exception.DomainException;
import com.facturacionAutonomo.auth_service.domain.exception.EmailYaRegistradoException;
import com.facturacionAutonomo.auth_service.domain.exception.RefreshTokenInvalidoException;
import com.facturacionAutonomo.auth_service.domain.exception.TenantNoEncontradoException;
import com.facturacionAutonomo.auth_service.infrastructure.web.response.ApiResponse;
import com.facturacionAutonomo.auth_service.infrastructure.web.response.ResponseUtil;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(DomainException.class)
	public ResponseEntity<ApiResponse<Object>> handleDomainException(DomainException ex) {
		HttpStatus status = statusDe(ex);
		log.debug("[handler] {} -> {}", ex.getClass().getSimpleName(), status.value());
		return respuesta(status, ex.getMessage());
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

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Object>> handleInesperada(Exception ex) {
		log.error("[handler] Error inesperado", ex);
		return respuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
	}

	private static HttpStatus statusDe(DomainException ex) {
		if (ex instanceof CredencialesInvalidasException || ex instanceof RefreshTokenInvalidoException) {
			return HttpStatus.UNAUTHORIZED;
		}
		if (ex instanceof CuentaBloqueadaException) {
			return HttpStatus.LOCKED;
		}
		if (ex instanceof CuentaExpiradaException) {
			return HttpStatus.FORBIDDEN;
		}
		if (ex instanceof EmailYaRegistradoException) {
			return HttpStatus.CONFLICT;
		}
		if (ex instanceof TenantNoEncontradoException) {
			return HttpStatus.NOT_FOUND;
		}
		if (ex instanceof DatoInvalidoException) {
			return HttpStatus.BAD_REQUEST;
		}
		return HttpStatus.BAD_REQUEST;
	}

	private static ResponseEntity<ApiResponse<Object>> respuesta(HttpStatus status, String mensaje) {
		return ResponseEntity.status(status).body(ResponseUtil.error(mensaje, status.value()));
	}
}
