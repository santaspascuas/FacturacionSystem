package com.facturacionAutonomo.auth_service.infrastructure.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.auth_service.application.port.out.UserRepositoryPort;
import com.facturacionAutonomo.auth_service.domain.exception.EmailYaRegistradoException;
import com.facturacionAutonomo.auth_service.domain.exception.TenantNoEncontradoException;
import com.facturacionAutonomo.auth_service.domain.model.Email;
import com.facturacionAutonomo.auth_service.domain.model.User;
import com.facturacionAutonomo.auth_service.infrastructure.persistence.entity.TenantEntity;
import com.facturacionAutonomo.auth_service.infrastructure.persistence.entity.UserEntity;
import com.facturacionAutonomo.auth_service.infrastructure.persistence.mapper.UserEntityMapper;
import com.facturacionAutonomo.auth_service.infrastructure.persistence.repository.SpringDataTenantRepository;
import com.facturacionAutonomo.auth_service.infrastructure.persistence.repository.SpringDataUserRepository;

@Repository
public class JpaUserRepositoryAdapter implements UserRepositoryPort{
	private static final Logger log = LoggerFactory.getLogger(JpaUserRepositoryAdapter.class);

	private final SpringDataUserRepository springdataUserRepository;

	private final SpringDataTenantRepository springdataTenantRepository;

	public JpaUserRepositoryAdapter(SpringDataUserRepository springdataUserRepository,
			SpringDataTenantRepository springdataTenantRepository) {
		super();
		this.springdataUserRepository = springdataUserRepository;
		this.springdataTenantRepository = springdataTenantRepository;
	}

	@Override
	@Transactional
	public User save(User user) {
		log.info("[Proceso de guardado en la entity User]");
		log.debug("USER: {}", user.getEmail());

		TenantEntity tenant = springdataTenantRepository.findByPublicId(user.getTenantId()).orElse(null);

		if (tenant == null) {
			throw new TenantNoEncontradoException();
		}

		UserEntity userEntity = springdataUserRepository.findByPublicId(user.getId()).orElse(null);

		if (userEntity == null) {
			userEntity = new UserEntity();
			log.debug("[save] No existe el usuario -> se crea");
		} else {
			log.debug("[save] Existe el usuario -> se actualiza");
		}

		UserEntityMapper.cambioEntityJpa(user, tenant, userEntity);

		try {
			UserEntity guardado = springdataUserRepository.saveAndFlush(userEntity);
			return UserEntityMapper.cambioEntidadDominio(guardado);
		} catch (DataIntegrityViolationException ex) {
			log.warn("[save] Violacion de restriccion al guardar el usuario {}", user.getId());
			throw new EmailYaRegistradoException();
		}
	}

	@Override
	public Optional<User> findByEmail(Email email) {
		return springdataUserRepository.findByEmail(email.value())
			.map(UserEntityMapper::cambioEntidadDominio);
	}

	@Override
	public boolean existsByEmail(Email email) {
		return springdataUserRepository.existsByEmail(email.value());
	}

	@Override
	public Optional<User> findById(UUID id) {
		return springdataUserRepository.findByPublicId(id)
			.map(UserEntityMapper::cambioEntidadDominio);
	}
}
