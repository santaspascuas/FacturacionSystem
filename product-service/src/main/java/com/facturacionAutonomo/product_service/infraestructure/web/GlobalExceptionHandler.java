package com.facturacionAutonomo.product_service.infraestructure.web;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.facturacionAutonomo.product_service.domain.exception.CategoryException;
import com.facturacionAutonomo.product_service.domain.exception.CategoryNotFoundException;
import com.facturacionAutonomo.product_service.domain.exception.CategoryYaRegistradoException;
import com.facturacionAutonomo.product_service.domain.exception.MenuItemException;
import com.facturacionAutonomo.product_service.domain.exception.MenuItemNotFoundException;
import com.facturacionAutonomo.product_service.domain.exception.MenuItemYaRegistradoException;
import com.facturacionAutonomo.product_service.domain.exception.ModifierException;
import com.facturacionAutonomo.product_service.domain.exception.ModifierNoAsignadoException;
import com.facturacionAutonomo.product_service.domain.exception.ModifierNotFoundException;
import com.facturacionAutonomo.product_service.domain.exception.ModifierYaAsignadoException;
import com.facturacionAutonomo.product_service.domain.exception.ModifierYaRegistradoException;
import com.facturacionAutonomo.product_service.domain.exception.TableException;
import com.facturacionAutonomo.product_service.domain.exception.MesaConPedidoAbiertoException;
import com.facturacionAutonomo.product_service.domain.exception.OrderException;
import com.facturacionAutonomo.product_service.domain.exception.OrderNotFoundException;
import com.facturacionAutonomo.product_service.domain.exception.OrderYaRegistradaException;
import com.facturacionAutonomo.product_service.domain.exception.TableConPedidosException;
import com.facturacionAutonomo.product_service.domain.exception.TableNotFoundException;
import com.facturacionAutonomo.product_service.domain.exception.TableYaRegistradaException;
import com.facturacionAutonomo.product_service.domain.exception.UserTpvException;
import com.facturacionAutonomo.product_service.infraestructure.utilitys.ApiResponse;
import com.facturacionAutonomo.product_service.infraestructure.utilitys.ResponseUtil;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	
	@ExceptionHandler(UserTpvException.class)
	public ResponseEntity<ApiResponse<Object>> handleUserTpvException(UserTpvException ex){
		return respuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
	}
	
	@ExceptionHandler(CategoryException.class)
	public ResponseEntity<ApiResponse<Object>> handleCategoryException(CategoryException ex){
		return respuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(CategoryYaRegistradoException.class)
	public ResponseEntity<ApiResponse<Object>> handleCategoryYaRegistrado(CategoryYaRegistradoException ex){
		return respuesta(HttpStatus.CONFLICT, ex.getMessage());
	}

	@ExceptionHandler(CategoryNotFoundException.class)
	public ResponseEntity<ApiResponse<Object>> handleCategoryNotFound(CategoryNotFoundException ex){
		return respuesta(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(MenuItemException.class)
	public ResponseEntity<ApiResponse<Object>> handleMenuItemException(MenuItemException ex){
		return respuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(MenuItemYaRegistradoException.class)
	public ResponseEntity<ApiResponse<Object>> handleMenuItemYaRegistrado(MenuItemYaRegistradoException ex){
		return respuesta(HttpStatus.CONFLICT, ex.getMessage());
	}

	@ExceptionHandler(MenuItemNotFoundException.class)
	public ResponseEntity<ApiResponse<Object>> handleMenuItemNotFound(MenuItemNotFoundException ex){
		return respuesta(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(TableException.class)
	public ResponseEntity<ApiResponse<Object>> handleTableException(TableException ex){
		return respuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(TableYaRegistradaException.class)
	public ResponseEntity<ApiResponse<Object>> handleTableYaRegistrada(TableYaRegistradaException ex){
		return respuesta(HttpStatus.CONFLICT, ex.getMessage());
	}

	@ExceptionHandler(OrderNotFoundException.class)
	public ResponseEntity<ApiResponse<Object>> handleOrderNotFound(OrderNotFoundException ex){
		return respuesta(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(MesaConPedidoAbiertoException.class)
	public ResponseEntity<ApiResponse<Object>> handleMesaConPedidoAbierto(MesaConPedidoAbiertoException ex){
		return respuesta(HttpStatus.CONFLICT, ex.getMessage());
	}

	@ExceptionHandler(OrderYaRegistradaException.class)
	public ResponseEntity<ApiResponse<Object>> handleOrderYaRegistrada(OrderYaRegistradaException ex){
		return respuesta(HttpStatus.CONFLICT, ex.getMessage());
	}

	@ExceptionHandler(OrderException.class)
	public ResponseEntity<ApiResponse<Object>> handleOrderException(OrderException ex){
		return respuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(TableConPedidosException.class)
	public ResponseEntity<ApiResponse<Object>> handleTableConPedidos(TableConPedidosException ex){
		return respuesta(HttpStatus.CONFLICT, ex.getMessage());
	}

	@ExceptionHandler(TableNotFoundException.class)
	public ResponseEntity<ApiResponse<Object>> handleTableNotFound(TableNotFoundException ex){
		return respuesta(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(ModifierException.class)
	public ResponseEntity<ApiResponse<Object>> handleModifierException(ModifierException ex){
		return respuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(ModifierNoAsignadoException.class)
	public ResponseEntity<ApiResponse<Object>> handleModifierNoAsignado(ModifierNoAsignadoException ex){
		return respuesta(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(ModifierYaRegistradoException.class)
	public ResponseEntity<ApiResponse<Object>> handleModifierYaRegistrado(ModifierYaRegistradoException ex){
		return respuesta(HttpStatus.CONFLICT, ex.getMessage());
	}

	@ExceptionHandler(ModifierYaAsignadoException.class)
	public ResponseEntity<ApiResponse<Object>> handleModifierYaAsignado(ModifierYaAsignadoException ex){
		return respuesta(HttpStatus.CONFLICT, ex.getMessage());
	}

	@ExceptionHandler(ModifierNotFoundException.class)
	public ResponseEntity<ApiResponse<Object>> handleModifierNotFound(ModifierNotFoundException ex){
		return respuesta(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	// Errores de la petición, como en customer-service: el cliente recibe el ApiResponse y no el error por defecto de Spring.

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

	// @Version: otra petición guardó el mismo registro entre que este se leyó y se guardó.
	// Sirve para todas las entidades (mesas, productos, categorías, modificadores).
	@ExceptionHandler(ObjectOptimisticLockingFailureException.class)
	public ResponseEntity<ApiResponse<Object>> handleConcurrencia(ObjectOptimisticLockingFailureException ex) {
		log.debug("[handler] Conflicto de concurrencia: {}", ex.getMessage());
		return respuesta(HttpStatus.CONFLICT, "El registro ha cambiado mientras se guardaba, vuelve a cargarlo");
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
	
	
	
	private static ResponseEntity<ApiResponse<Object>> respuesta(HttpStatus status, String mensaje) {
		return ResponseEntity.status(status).body(ResponseUtil.error(mensaje, status.value()));
	}
	

}
