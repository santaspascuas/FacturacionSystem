package com.facturacionAutonomo.product_service.infraestructure.persistence.entitys;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * Un tipo de IVA y desde cuándo se aplica. No es un dato de un tenant: es el mismo para toda la plataforma.
 * Las filas no se modifican: un cambio de tipo es una fila nueva con su fecha, así queda el historial.
 * Las restricciones reales viven en la migración V8 (ddl-auto=validate).
 */
@Entity
@Table(name = "tax_rates")
public class TaxRateEntity {

	@Id
	@Column(name = "tax_rate_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tax_rate_id_generator")
	@SequenceGenerator(name = "tax_rate_id_generator", sequenceName = "tax_rate_id_seq", allocationSize = 1)
	private Long taxRateId;

	@Column(nullable = false, updatable = false, precision = 5, scale = 2)
	private BigDecimal rate;

	// En UTC, como el resto de fechas del servicio.
	@Column(name = "valid_from", nullable = false, unique = true, updatable = false)
	private LocalDateTime validFrom;

	public TaxRateEntity() {
		super();
	}

	public Long getTaxRateId() {
		return taxRateId;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public LocalDateTime getValidFrom() {
		return validFrom;
	}

	@Override
	public String toString() {
		return "TaxRateEntity [taxRateId=" + taxRateId + ", rate=" + rate + ", validFrom=" + validFrom + "]";
	}

}
