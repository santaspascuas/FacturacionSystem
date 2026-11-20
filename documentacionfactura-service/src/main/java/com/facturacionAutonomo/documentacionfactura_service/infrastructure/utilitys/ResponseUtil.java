package com.facturacionAutonomo.documentacionfactura_service.infrastructure.utilitys;

import java.time.LocalDateTime;

public class ResponseUtil {

	private ResponseUtil() {}

	public static <T> ApiResponse<T> success(String message, T data) {
		return construir(true, message, data, null);
	}

	public static <T> ApiResponse<T> error(String message, int errorCode) {
		return construir(false, message, null, errorCode);
	}

	private static <T> ApiResponse<T> construir(boolean success, String message, T data, Integer errorCode) {
		ApiResponse<T> respuesta = new ApiResponse<>();
		respuesta.setSuccess(success);
		respuesta.setMessage(message);
		respuesta.setData(data);
		respuesta.setErrorCode(errorCode);
		respuesta.setTimestamp(LocalDateTime.now());
		return respuesta;
	}
}
