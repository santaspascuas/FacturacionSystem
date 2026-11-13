package com.facturacionAutonomo.product_service.infraestructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.MenuItemModifierEntity;

public interface SpringDataMenuItemModifierEntityRepository extends JpaRepository<MenuItemModifierEntity, Long>{

	// MenuItem_MenuitemsId y Modifier_ModificadorId navegan las relaciones hasta su id
	// (se filtra por la clave foránea, sin JOIN).
	boolean existsByMenuItem_MenuitemsIdAndModifier_ModificadorId(Long menuItemId, Long modifierId);

	Optional<MenuItemModifierEntity> findByMenuItem_MenuitemsIdAndModifier_ModificadorId(Long menuItemId, Long modifierId);

	List<MenuItemModifierEntity> findByMenuItem_MenuitemsIdOrderByMenuItemModifierIdAsc(Long menuItemId);

}
