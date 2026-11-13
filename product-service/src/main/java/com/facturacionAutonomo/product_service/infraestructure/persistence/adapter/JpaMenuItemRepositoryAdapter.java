package com.facturacionAutonomo.product_service.infraestructure.persistence.adapter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.facturacionAutonomo.product_service.application.port.OUT.MenuItemRepositoryPort;
import com.facturacionAutonomo.product_service.domain.exception.MenuItemNotFoundException;
import com.facturacionAutonomo.product_service.domain.model.MenuItem;
import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.MenuCategoryEntity;
import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.MenuItemEntity;
import com.facturacionAutonomo.product_service.infraestructure.persistence.mapper.MenuItemsEntityMapper;
import com.facturacionAutonomo.product_service.infraestructure.persistence.repository.SpringDataMenuCategoryRepository;
import com.facturacionAutonomo.product_service.infraestructure.persistence.repository.SpringDataMenuItemRepository;

@Repository
public class JpaMenuItemRepositoryAdapter implements MenuItemRepositoryPort{

	private static final Logger log = LoggerFactory.getLogger(JpaMenuItemRepositoryAdapter.class);

	private final SpringDataMenuItemRepository springitemrepository;
	private final SpringDataMenuCategoryRepository springcategoryrepository;


	public JpaMenuItemRepositoryAdapter(SpringDataMenuItemRepository springitemrepository,
			SpringDataMenuCategoryRepository springcategoryrepository) {
		super();
		this.springitemrepository = springitemrepository;
		this.springcategoryrepository = springcategoryrepository;
	}


	@Override
	public MenuItem save(MenuItem item) {
		log.debug("Guardando el producto {} del tenant {}", item.getName(), item.getTenantId());
		// getReferenceById no consulta la categoría: solo hace falta su id para la clave foránea.
		// Que exista y sea del tenant lo comprueba el servicio antes de llamar aquí.
		MenuCategoryEntity categoria = springcategoryrepository.getReferenceById(item.getCategoryId());
		MenuItemEntity creado = MenuItemsEntityMapper.dominiotoJpa(item, categoria);
		MenuItemEntity guardado = springitemrepository.saveAndFlush(creado);
		return MenuItemsEntityMapper.jpaToDominio(guardado);
	}


	@Override
	public MenuItem update(MenuItem item) {
		log.debug("Actualizando el producto {} del tenant {}", item.getName(), item.getTenantId());
		// Se parte de la entidad gestionada para conservar su @Version: una entidad nueva con version null
		// la trataría Hibernate como nueva y haría un INSERT.
		MenuItemEntity actualizado = springitemrepository.findById(item.getId())
				.orElseThrow(() -> new MenuItemNotFoundException("El producto no se ha encontrado"));
		actualizado.setCategory(springcategoryrepository.getReferenceById(item.getCategoryId()));
		actualizado.setName(item.getName());
		actualizado.setDescription(item.getDescription());
		actualizado.setBasePrice(item.getBasePrice());
		actualizado.setActive(item.isActive());
		actualizado.setImageUrl(item.getImageUrl());
		actualizado.setUpdatedAt(item.getUpdatedAt());
		return MenuItemsEntityMapper.jpaToDominio(springitemrepository.saveAndFlush(actualizado));
	}


	@Override
	public boolean existsActiveByTenantIdAndName(UUID tenantId, String name) {
		return springitemrepository.existsByTenantIdAndNameAndActiveTrue(tenantId, name);
	}


	@Override
	public Optional<MenuItem> findByTenantIdAndPublicId(UUID tenantId, UUID publicId) {
		return springitemrepository.findByTenantIdAndPublicId(tenantId, publicId)
				.map(MenuItemsEntityMapper::jpaToDominio);
	}


	@Override
	public List<MenuItem> findActiveByTenantId(UUID tenantId) {
		return springitemrepository.findByTenantIdAndActiveTrueOrderByNameAsc(tenantId).stream()
				.map(MenuItemsEntityMapper::jpaToDominio).toList();
	}


	@Override
	public List<MenuItem> findActiveByTenantIdAndCategoryId(UUID tenantId, Long categoryId) {
		return springitemrepository.findByTenantIdAndCategory_CategoryIdAndActiveTrueOrderByNameAsc(tenantId, categoryId).stream()
				.map(MenuItemsEntityMapper::jpaToDominio).toList();
	}


	// El mapper lee category.getCategoryId() del proxy LAZY sin lanzar consulta: sigue siendo una sola consulta.
	@Override
	public List<MenuItem> findActiveByTenantIdAndPublicIds(UUID tenantId, Collection<UUID> publicIds) {
		if(publicIds.isEmpty()) {
			return List.of();
		}
		return springitemrepository.findByTenantIdAndPublicIdInAndActiveTrue(tenantId, publicIds).stream()
				.map(MenuItemsEntityMapper::jpaToDominio).toList();
	}

}
