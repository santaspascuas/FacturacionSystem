package com.facturacionAutonomo.product_service.infraestructure.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.product_service.application.port.OUT.UserTpvRepositoryPort;
import com.facturacionAutonomo.product_service.domain.exception.UserTpvException;
import com.facturacionAutonomo.product_service.domain.model.UsersTpv;
import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.UserTpvEntity;
import com.facturacionAutonomo.product_service.infraestructure.persistence.mapper.UserTpvEntityMapper;
import com.facturacionAutonomo.product_service.infraestructure.persistence.repository.SrpingDataUserTpvRepository;

@Repository
public class JpaUsertpvRepositoryAdapter implements UserTpvRepositoryPort{
	
	private static final Logger log = LoggerFactory.getLogger(JpaUsertpvRepositoryAdapter.class);
	
	private final SrpingDataUserTpvRepository springdatauserrepository;
	
	
	

	public JpaUsertpvRepositoryAdapter(SrpingDataUserTpvRepository springdatauserrepository) {
		super();
		this.springdatauserrepository = springdatauserrepository;
	}

	@Override
	@Transactional
	public UsersTpv save(UUID tenantId, UsersTpv usertpv) {
		try {
			
			UserTpvEntity creado = UserTpvEntityMapper.dominioToJpa(usertpv);
			UserTpvEntity guardado = springdatauserrepository.save(creado);
			return UserTpvEntityMapper.jpaToDominio(guardado);	
			
		}catch(DataIntegrityViolationException ex) {
			throw new UserTpvException("El usuario " + usertpv.getUsername() + " ya está registrado");
			
		}
	}

	@Override
	public boolean existsByTenantIdAndUsername(UUID tenantId, String username) {
		// TODO Auto-generated method stub
		return springdatauserrepository.existsByTenantIdAndUsername(tenantId, username);
	}

	@Override
	public Optional<UsersTpv> findByByTenantIdAndUsername(UUID tenantId, String username) {
		// TODO Auto-generated method stub
		
		return springdatauserrepository.findByTenantIdAndUsername(tenantId, username)
				.map(UserTpvEntityMapper::jpaToDominio);
	}

}
