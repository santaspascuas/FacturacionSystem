package com.facturacionAutonomo.product_service.infraestructure.persistence.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.TaxRateEntity;

public interface SpringDataTaxRateRepository extends JpaRepository<TaxRateEntity, Long> {

	// El tipo vigente es el último que ya ha entrado en vigor:
	//   WHERE valid_from <= :momento ORDER BY valid_from DESC LIMIT 1
	// Los de fecha futura se ignoran hasta que llega su día. Usa el índice del UNIQUE de valid_from.
	Optional<TaxRateEntity> findFirstByValidFromLessThanEqualOrderByValidFromDesc(LocalDateTime momento);

}
