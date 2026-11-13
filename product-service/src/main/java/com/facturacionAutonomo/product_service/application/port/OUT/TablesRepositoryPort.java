package com.facturacionAutonomo.product_service.application.port.OUT;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.TableRestautant;
import com.facturacionAutonomo.product_service.domain.model.TableStatus;


public interface TablesRepositoryPort {

	TableRestautant save(TableRestautant mesa);
	TableRestautant update(TableRestautant mesa);
	boolean existsByTenantIdAndTableNumber(UUID tenantId, int tableNumber);
	Optional<TableRestautant> findByTenantIdAndPublicId(UUID tenantId, UUID publicId);
	List<TableRestautant> findByTenantId(UUID tenantId);
	List<TableRestautant> findByTenantIdAndStatus(UUID tenantId, TableStatus status);
	void deleteById(Long id);
	List<TableRestautant> findByIds(Collection<Long> ids);
	boolean tienePedidos(Long tableId);

}
