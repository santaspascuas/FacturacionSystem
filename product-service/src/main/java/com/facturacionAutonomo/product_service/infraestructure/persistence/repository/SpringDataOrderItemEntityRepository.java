package com.facturacionAutonomo.product_service.infraestructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.OrderItemEntity;

public interface SpringDataOrderItemEntityRepository extends JpaRepository<OrderItemEntity, Long>{
	
	Optional<OrderItemEntity>findByPublicId(UUID publicId);
	

}
