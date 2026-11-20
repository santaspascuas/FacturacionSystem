package com.facturacionAutonomo.documentacionfactura_service.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Sobre de respuesta de product-service ({ success, message, data, errorCode, timestamp }). Lo útil va en data.
 * Estos DTO solo los usan los adaptadores HTTP: nunca salen de infrastructure/client.
 *
 * ignoreUnknown: si el otro servicio añade un campo nuevo, esta respuesta se sigue leyendo (lector tolerante).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RemoteApiResponse<T>(boolean success, String message, T data, Integer errorCode) {
}
