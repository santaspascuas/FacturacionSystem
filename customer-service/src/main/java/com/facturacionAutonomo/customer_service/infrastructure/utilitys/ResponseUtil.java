package com.facturacionAutonomo.customer_service.infrastructure.utilitys;

import java.time.LocalDateTime;

public class ResponseUtil {
	public static<T> ApiResponse<T> success(String message, T data){
		ApiResponse<T> respuesta = new ApiResponse<>();
		respuesta.setData(data);
		respuesta.setErrorCode(null);
		respuesta.setSuccess(true);
		respuesta.setMessage(message);
		respuesta.setTimestamp(LocalDateTime.now());
		return respuesta;
	}

	public static<T> ApiResponse<T> error(String message, int errorCode){
		ApiResponse<T> respuesta = new ApiResponse<>();
		respuesta.setData(null);
		respuesta.setErrorCode(errorCode);
		respuesta.setSuccess(false);
		respuesta.setMessage(message);
		respuesta.setTimestamp(LocalDateTime.now());
		return respuesta;
	}
}
