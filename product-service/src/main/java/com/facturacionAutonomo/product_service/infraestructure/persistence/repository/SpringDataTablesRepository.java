package com.facturacionAutonomo.product_service.infraestructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.facturacionAutonomo.product_service.domain.model.TableStatus;
import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.TablesEntity;

// Spring Data genera la consulta a partir del nombre del método: cada parte (TenantId, TableNumber...)
// tiene que coincidir con un campo de TablesEntity y cada criterio necesita su parámetro, en el mismo orden.
public interface SpringDataTablesRepository extends JpaRepository<TablesEntity, Long>{
	
	boolean existsByTenantIdAndTableNumber(UUID tenantId, Integer tableNumber);
	Optional<TablesEntity> findByTenantIdAndPublicId(UUID tenantId, UUID publicId);
	List<TablesEntity> findByTenantIdOrderByZoneAscTableNumberAsc(UUID tenantId);
	List<TablesEntity> findByTenantIdAndStatusOrderByZoneAscTableNumberAsc(UUID tenantId, TableStatus status);

	// Si la mesa aparece en algún pedido (abierto o ya cerrado). Usa el índice idx_orders_table de la V4.
	@Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END FROM OrderEntity o WHERE o.table.tableId = :tableId")
	boolean existsOrdersByTableId(@Param("tableId") Long tableId);

}
