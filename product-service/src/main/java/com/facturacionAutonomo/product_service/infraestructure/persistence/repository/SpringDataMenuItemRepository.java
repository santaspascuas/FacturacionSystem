package com.facturacionAutonomo.product_service.infraestructure.persistence.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.MenuItemEntity;

public interface SpringDataMenuItemRepository extends JpaRepository<MenuItemEntity, Long>{

	boolean existsByTenantIdAndNameAndActiveTrue(UUID tenantId, String name);

	Optional<MenuItemEntity> findByTenantIdAndPublicId(UUID tenantId, UUID publicId);

	List<MenuItemEntity> findByTenantIdAndActiveTrueOrderByNameAsc(UUID tenantId);

	// Category_CategoryId navega la relación category hasta su id.
	List<MenuItemEntity> findByTenantIdAndCategory_CategoryIdAndActiveTrueOrderByNameAsc(UUID tenantId, Long categoryId);

	// PublicIdIn genera un WHERE public_id IN (...): todos los productos de las líneas de un pedido en una consulta.
	List<MenuItemEntity> findByTenantIdAndPublicIdInAndActiveTrue(UUID tenantId, Collection<UUID> publicIds);

}
