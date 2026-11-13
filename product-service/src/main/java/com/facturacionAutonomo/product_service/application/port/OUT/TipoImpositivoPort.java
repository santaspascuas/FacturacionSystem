package com.facturacionAutonomo.product_service.application.port.OUT;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Tipo de IVA (en %) vigente en un momento dado. Lo marca la ley y cambia en una fecha concreta, así que no va
 * fijo en el dominio: se guarda con su fecha de entrada en vigor y se busca el que aplica al momento del pedido.
 * Es el mismo para todos los tenants (todos tributan con IVA peninsular).
 */
public interface TipoImpositivoPort {

	// momento en UTC, el mismo que usa el servicio para las fechas del pedido (Clock).
	BigDecimal tipoVigenteEn(LocalDateTime momento);

}
