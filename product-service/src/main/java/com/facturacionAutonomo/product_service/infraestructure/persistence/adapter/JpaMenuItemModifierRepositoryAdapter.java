package com.facturacionAutonomo.product_service.infraestructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import com.facturacionAutonomo.product_service.application.port.OUT.MenuItemModifierRepositoryPort;
import com.facturacionAutonomo.product_service.domain.exception.ModifierNoAsignadoException;
import com.facturacionAutonomo.product_service.domain.exception.ModifierYaAsignadoException;
import com.facturacionAutonomo.product_service.domain.model.MenuItemModifier;
import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.MenuItemEntity;
import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.MenuItemModifierEntity;
import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.ModifierEntity;
import com.facturacionAutonomo.product_service.infraestructure.persistence.mapper.MenuItemModifierEntityMapper;
import com.facturacionAutonomo.product_service.infraestructure.persistence.repository.SpringDataMenuItemModifierEntityRepository;
import com.facturacionAutonomo.product_service.infraestructure.persistence.repository.SpringDataMenuItemRepository;
import com.facturacionAutonomo.product_service.infraestructure.persistence.repository.SpringDataModifierRepository;

@Repository
public class JpaMenuItemModifierRepositoryAdapter implements MenuItemModifierRepositoryPort{

	private static final Logger log = LoggerFactory.getLogger(JpaMenuItemModifierRepositoryAdapter.class);

	private final SpringDataMenuItemModifierEntityRepository springmenuitemmodifierrepository;
	private final SpringDataMenuItemRepository springitemrepository;
	private final SpringDataModifierRepository springmodifierrepository;


	public JpaMenuItemModifierRepositoryAdapter(SpringDataMenuItemModifierEntityRepository springmenuitemmodifierrepository,
			SpringDataMenuItemRepository springitemrepository, SpringDataModifierRepository springmodifierrepository) {
		super();
		this.springmenuitemmodifierrepository = springmenuitemmodifierrepository;
		this.springitemrepository = springitemrepository;
		this.springmodifierrepository = springmodifierrepository;
	}


	@Override
	public MenuItemModifier save(MenuItemModifier menuItemModifier) {
		log.debug("Asociando el modificador {} al producto {}", menuItemModifier.getModifierId(), menuItemModifier.getMenuItemId());
		// getReferenceById no consulta: solo hacen falta los ids para las claves foráneas.
		// Que existan y sean del mismo tenant lo comprueba el servicio antes de llamar aquí.
		MenuItemEntity producto = springitemrepository.getReferenceById(menuItemModifier.getMenuItemId());
		ModifierEntity modificador = springmodifierrepository.getReferenceById(menuItemModifier.getModifierId());
		MenuItemModifierEntity creado = MenuItemModifierEntityMapper.dominioToJpa(menuItemModifier, producto, modificador);
		try {
			// saveAndFlush lanza el INSERT aquí dentro; con save() la violación saltaría en el commit, fuera del catch.
			return MenuItemModifierEntityMapper.jpaToDominio(springmenuitemmodifierrepository.saveAndFlush(creado));
		}catch(DataIntegrityViolationException ex) {
			throw new ModifierYaAsignadoException("El modificador ya está asociado a este producto");
		}
	}


	@Override
	public MenuItemModifier update(MenuItemModifier menuItemModifier) {
		log.debug("Actualizando la asociación {}", menuItemModifier.getId());
		// Solo cambian las reglas; el producto y el modificador de una asociación no se cambian.
		MenuItemModifierEntity actualizado = springmenuitemmodifierrepository.findById(menuItemModifier.getId())
				.orElseThrow(() -> new ModifierNoAsignadoException("El modificador no está asociado a este producto"));
		actualizado.setMaxSelections(menuItemModifier.getMaxSelections());
		actualizado.setRequired(menuItemModifier.isRequired());
		return MenuItemModifierEntityMapper.jpaToDominio(springmenuitemmodifierrepository.saveAndFlush(actualizado));
	}


	@Override
	public boolean existsByMenuItemIdAndModifierId(Long menuItemId, Long modifierId) {
		return springmenuitemmodifierrepository.existsByMenuItem_MenuitemsIdAndModifier_ModificadorId(menuItemId, modifierId);
	}


	@Override
	public Optional<MenuItemModifier> findByMenuItemIdAndModifierId(Long menuItemId, Long modifierId) {
		return springmenuitemmodifierrepository.findByMenuItem_MenuitemsIdAndModifier_ModificadorId(menuItemId, modifierId)
				.map(MenuItemModifierEntityMapper::jpaToDominio);
	}


	@Override
	public List<MenuItemModifier> findByMenuItemId(Long menuItemId) {
		return springmenuitemmodifierrepository.findByMenuItem_MenuitemsIdOrderByMenuItemModifierIdAsc(menuItemId).stream()
				.map(MenuItemModifierEntityMapper::jpaToDominio).toList();
	}


	@Override
	public void deleteById(Long id) {
		log.debug("Eliminando la asociación {}", id);
		springmenuitemmodifierrepository.deleteById(id);
	}

}
