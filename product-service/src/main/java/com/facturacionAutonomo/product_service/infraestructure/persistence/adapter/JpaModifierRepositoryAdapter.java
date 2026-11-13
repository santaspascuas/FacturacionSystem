package com.facturacionAutonomo.product_service.infraestructure.persistence.adapter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import com.facturacionAutonomo.product_service.application.port.OUT.ModifierRepositoryPort;
import com.facturacionAutonomo.product_service.domain.exception.ModifierNotFoundException;
import com.facturacionAutonomo.product_service.domain.exception.ModifierYaRegistradoException;
import com.facturacionAutonomo.product_service.domain.model.Modifier;
import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.ModifierEntity;
import com.facturacionAutonomo.product_service.infraestructure.persistence.mapper.ModifierEntityMapper;
import com.facturacionAutonomo.product_service.infraestructure.persistence.repository.SpringDataModifierRepository;

@Repository
public class JpaModifierRepositoryAdapter implements ModifierRepositoryPort{

	private static final Logger log = LoggerFactory.getLogger(JpaModifierRepositoryAdapter.class);

	private final SpringDataModifierRepository springmodifierrepository;


	public JpaModifierRepositoryAdapter(SpringDataModifierRepository springmodifierrepository) {
		super();
		this.springmodifierrepository = springmodifierrepository;
	}


	@Override
	public Modifier save(Modifier modifier) {
		log.debug("Guardando el modificador {} del tenant {}", modifier.getName(), modifier.getTenantId());
		ModifierEntity creado = ModifierEntityMapper.dominioToJpa(modifier);
		try {
			// saveAndFlush lanza el INSERT aquí dentro; con save() la violación saltaría en el commit, fuera del catch.
			return ModifierEntityMapper.jpaToDominio(springmodifierrepository.saveAndFlush(creado));
		}catch(DataIntegrityViolationException ex) {
			// El servicio ya comprueba el nombre; esto cubre dos altas a la vez con el mismo nombre.
			throw new ModifierYaRegistradoException("El modificador " + modifier.getName() + " ya está registrado");
		}
	}


	@Override
	public Modifier update(Modifier modifier) {
		log.debug("Actualizando el modificador {} del tenant {}", modifier.getName(), modifier.getTenantId());
		// Se parte de la entidad gestionada para conservar su @Version: una entidad nueva con version null
		// la trataría Hibernate como nueva y haría un INSERT.
		ModifierEntity actualizado = springmodifierrepository.findById(modifier.getId())
				.orElseThrow(() -> new ModifierNotFoundException("El modificador no se ha encontrado"));
		actualizado.setName(modifier.getName());
		actualizado.setModifierType(modifier.getModifierType());
		actualizado.setPriceDelta(modifier.getPriceDelta());
		actualizado.setActive(modifier.isActive());
		actualizado.setUpdatedAt(modifier.getUpdatedAt());
		try {
			return ModifierEntityMapper.jpaToDominio(springmodifierrepository.saveAndFlush(actualizado));
		}catch(DataIntegrityViolationException ex) {
			throw new ModifierYaRegistradoException("El modificador " + modifier.getName() + " ya está registrado");
		}
	}


	@Override
	public boolean existsByTenantIdAndName(UUID tenantId, String name) {
		return springmodifierrepository.existsByTenantIdAndName(tenantId, name);
	}


	@Override
	public Optional<Modifier> findByTenantIdAndPublicId(UUID tenantId, UUID publicId) {
		return springmodifierrepository.findByTenantIdAndPublicId(tenantId, publicId)
				.map(ModifierEntityMapper::jpaToDominio);
	}


	@Override
	public List<Modifier> findActiveByTenantId(UUID tenantId) {
		return springmodifierrepository.findByTenantIdAndActiveTrueOrderByNameAsc(tenantId).stream()
				.map(ModifierEntityMapper::jpaToDominio).toList();
	}


	@Override
	public List<Modifier> findByIds(Collection<Long> ids) {
		if(ids.isEmpty()) {
			return List.of();
		}
		return springmodifierrepository.findAllById(ids).stream()
				.map(ModifierEntityMapper::jpaToDominio).toList();
	}

}
