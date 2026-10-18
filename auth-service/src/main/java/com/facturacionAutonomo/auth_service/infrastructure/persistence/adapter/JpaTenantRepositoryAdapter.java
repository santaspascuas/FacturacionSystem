package com.facturacionAutonomo.auth_service.infrastructure.persistence.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.auth_service.application.port.out.TenantRepositoryPort;
import com.facturacionAutonomo.auth_service.domain.model.Tenant;
import com.facturacionAutonomo.auth_service.infrastructure.persistence.entity.TenantEntity;
import com.facturacionAutonomo.auth_service.infrastructure.persistence.mapper.TenantEntityMapper;
import com.facturacionAutonomo.auth_service.infrastructure.persistence.repository.SpringDataTenantRepository;

@Repository
public class JpaTenantRepositoryAdapter implements TenantRepositoryPort{
	private final SpringDataTenantRepository springdataTenantRepository;
	private static final Logger log = LoggerFactory.getLogger(JpaTenantRepositoryAdapter.class);

	public JpaTenantRepositoryAdapter(SpringDataTenantRepository springdataTenantRepository) {
		super();
		this.springdataTenantRepository = springdataTenantRepository;
	}

	@Override
	@Transactional
	public Tenant save(Tenant tenant) {
		log.info("[Iniciamos la funcion del guardado de persistencia]");

		TenantEntity tenantEntity = springdataTenantRepository.findByPublicId(tenant.getId()).orElse(null);

		if (tenantEntity == null) {
			tenantEntity = new TenantEntity();
			log.debug("[save] No existe el tenant {} -> se crea", tenant.getId());
		} else {
			log.debug("[save] Existe el tenant {} -> se actualiza", tenant.getId());
		}

		TenantEntityMapper.dominiotoJpa(tenant, tenantEntity);
		TenantEntity guardado = springdataTenantRepository.save(tenantEntity);
		return  TenantEntityMapper.jpaEntityToDominio(guardado);
	}
}
