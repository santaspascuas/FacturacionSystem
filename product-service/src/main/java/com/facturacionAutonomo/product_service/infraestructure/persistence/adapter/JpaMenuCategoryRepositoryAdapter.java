package com.facturacionAutonomo.product_service.infraestructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import com.facturacionAutonomo.product_service.application.port.OUT.MenuCategoryRepositoryPort;
import com.facturacionAutonomo.product_service.domain.exception.CategoryNotFoundException;
import com.facturacionAutonomo.product_service.domain.exception.CategoryYaRegistradoException;
import com.facturacionAutonomo.product_service.domain.model.MenuCategory;
import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.MenuCategoryEntity;
import com.facturacionAutonomo.product_service.infraestructure.persistence.mapper.MenuCategoryEntityMapper;
import com.facturacionAutonomo.product_service.infraestructure.persistence.repository.SpringDataMenuCategoryRepository;

@Repository
public class JpaMenuCategoryRepositoryAdapter implements MenuCategoryRepositoryPort{

	private static final Logger log = LoggerFactory.getLogger(JpaMenuCategoryRepositoryAdapter.class);

	private final SpringDataMenuCategoryRepository springcategoryrepository;


	public JpaMenuCategoryRepositoryAdapter(SpringDataMenuCategoryRepository springcategoryrepository) {
		super();
		this.springcategoryrepository = springcategoryrepository;
	}


	@Override
	public MenuCategory save(MenuCategory category) {
		log.debug("Guardando la categoria {} del tenant {}", category.getName(), category.getTenantId());
		//TRanformamos el ed a jpa
		MenuCategoryEntity creado = MenuCategoryEntityMapper.dominioToJpa(category);
		try {
			// saveAndFlush lanza el INSERT aquí dentro; con save() la violación saltaría en el commit, fuera del catch.
			MenuCategoryEntity guardado = springcategoryrepository.saveAndFlush(creado);
			return MenuCategoryEntityMapper.jpaToDominio(guardado);

		}catch(DataIntegrityViolationException ex) {
			throw new CategoryYaRegistradoException("La categoría " + category.getName() + " ya está registrada");
		}
	}


	@Override
	public MenuCategory update(MenuCategory category) {
		log.debug("Actualizando la categoria {} del tenant {}", category.getName(), category.getTenantId());
		// Se parte de la entidad gestionada para conservar su @Version: una entidad nueva con version null
		// la trataría Hibernate como nueva y haría un INSERT.
		MenuCategoryEntity actualizada = springcategoryrepository.findById(category.getId())
				.orElseThrow(() -> new CategoryNotFoundException("La categoría no se ha encontrado"));
		actualizada.setName(category.getName());
		actualizada.setActive(category.isActive());
		actualizada.setDisplayOrder(category.getDisplayOrder());
		actualizada.setUpdatedAt(category.getUpdatedAt());
		try {
			return MenuCategoryEntityMapper.jpaToDominio(springcategoryrepository.saveAndFlush(actualizada));
		}catch(DataIntegrityViolationException ex) {
			throw new CategoryYaRegistradoException("La categoría " + category.getName() + " ya está registrada");
		}
	}


	@Override
	public boolean existsByTenantIdAndName(UUID tenantId, String name) {
		return springcategoryrepository.existsByTenantIdAndName(tenantId, name);
	}


	@Override
	public int findMaxDisplayOrderByTenantId(UUID tenantId) {
		return springcategoryrepository.findMaxDisplayOrderByTenantId(tenantId);
	}


	@Override
	public List<MenuCategory> findActiveByTenantId(UUID tenantId) {
		return springcategoryrepository.findByTenantIdAndActiveTrueOrderByDisplayOrderAscCategoryIdAsc(tenantId).stream()
				.map(MenuCategoryEntityMapper::jpaToDominio).toList();
	}


	@Override
	public Optional <MenuCategory> findByTenantIdAndPublicId(UUID tenantId, UUID publicId) {
		return springcategoryrepository.findByTenantIdAndPublicId(tenantId, publicId)
				.map(MenuCategoryEntityMapper::jpaToDominio);
	}








		
		
	


}
