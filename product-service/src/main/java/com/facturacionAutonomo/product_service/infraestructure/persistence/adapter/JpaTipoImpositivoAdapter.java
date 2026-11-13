package com.facturacionAutonomo.product_service.infraestructure.persistence.adapter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.facturacionAutonomo.product_service.application.port.OUT.TipoImpositivoPort;
import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.TaxRateEntity;
import com.facturacionAutonomo.product_service.infraestructure.persistence.repository.SpringDataTaxRateRepository;

/**
 * Lee el tipo de IVA vigente de la tabla tax_rates. Para cambiarlo no hace falta desplegar ni reiniciar:
 * se inserta una fila con el tipo nuevo y su fecha de entrada en vigor, y a partir de ese momento se aplica solo.
 */
@Repository
public class JpaTipoImpositivoAdapter implements TipoImpositivoPort {

	private static final Logger log = LoggerFactory.getLogger(JpaTipoImpositivoAdapter.class);

	private final SpringDataTaxRateRepository springtaxraterepository;

	public JpaTipoImpositivoAdapter(SpringDataTaxRateRepository springtaxraterepository) {
		super();
		this.springtaxraterepository = springtaxraterepository;
	}

	@Override
	public BigDecimal tipoVigenteEn(LocalDateTime momento) {
		TaxRateEntity vigente = springtaxraterepository.findFirstByValidFromLessThanEqualOrderByValidFromDesc(momento)
				// Sin ningún tipo vigente es un fallo de configuración del servidor, no un error del cliente:
				// IllegalStateException acaba en un 500, no en un 400.
				.orElseThrow(() -> new IllegalStateException("No hay ningún tipo de IVA vigente el " + momento));
		log.debug("Tipo de IVA vigente el {}: {}", momento, vigente);
		return vigente.getRate();
	}

}
