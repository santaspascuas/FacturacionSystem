package com.facturacionAutonomo.product_service.infraestructure.utilitys;

import java.time.LocalDateTime;


public class ResponseUtil {
	
	public static<T> ApiResponse<T>sucess(String message, T data){
		ApiResponse<T> respuesta = new ApiResponse<>();//De tipo generico como typescript
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
