package com.facturacionAutonomo.documentacionfactura_service.infrastructure.utilitys;

import java.time.LocalDateTime;

// Mismo sobre que customer-service y product-service: el front recibe igual las respuestas de todos.
public class ApiResponse<T> {
	private boolean success;
	private String message;
	private T data;
	private Integer errorCode;
	private LocalDateTime timestamp;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
}
