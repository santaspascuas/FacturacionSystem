package com.facturacionAutonomo.product_service.infraestructure.persistence.adapter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import com.facturacionAutonomo.product_service.application.port.OUT.TablesRepositoryPort;
import com.facturacionAutonomo.product_service.domain.exception.TableConPedidosException;
import com.facturacionAutonomo.product_service.domain.exception.TableNotFoundException;
import com.facturacionAutonomo.product_service.domain.exception.TableYaRegistradaException;
import com.facturacionAutonomo.product_service.domain.model.TableRestautant;
import com.facturacionAutonomo.product_service.domain.model.TableStatus;
import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.TablesEntity;
import com.facturacionAutonomo.product_service.infraestructure.persistence.mapper.TablesEntityMapper;
import com.facturacionAutonomo.product_service.infraestructure.persistence.repository.SpringDataTablesRepository;

@Repository
public class JpaTablesRepositoryAdapter implements TablesRepositoryPort{

	private static final Logger log = LoggerFactory.getLogger(JpaTablesRepositoryAdapter.class);

	private final SpringDataTablesRepository springtablesrepository;


	public JpaTablesRepositoryAdapter(SpringDataTablesRepository springtablesrepository) {
		super();
		this.springtablesrepository = springtablesrepository;
	}


	@Override
	public TableRestautant save(TableRestautant mesa) {
		log.debug("Guardando la mesa {} del tenant {}", mesa.getTableNumber(), mesa.getTenantId());
		TablesEntity creada = TablesEntityMapper.dominioToJpa(mesa);
		try {
			// saveAndFlush lanza el INSERT aquí dentro; con save() la violación saltaría en el commit, fuera del catch.
			return TablesEntityMapper.jpaToDominio(springtablesrepository.saveAndFlush(creada));
		}catch(DataIntegrityViolationException ex) {
			// El servicio ya comprueba el número; esto cubre dos altas a la vez con el mismo número.
			throw new TableYaRegistradaException("La mesa " + mesa.getTableNumber() + " ya está registrada");
		}
	}


	@Override
	public TableRestautant update(TableRestautant mesa) {
		log.debug("Actualizando la mesa {} del tenant {}", mesa.getTableNumber(), mesa.getTenantId());
		// Se parte de la entidad gestionada para conservar su @Version: una entidad nueva con version null
		// la trataría Hibernate como nueva y haría un INSERT.
		TablesEntity actualizada = springtablesrepository.findById(mesa.getId())
				.orElseThrow(() -> new TableNotFoundException("La mesa no se ha encontrado"));
		actualizada.setTableNumber(mesa.getTableNumber());
		actualizada.setCapacity(mesa.getCapacity());
		actualizada.setZone(mesa.getZone());
		actualizada.setStatus(mesa.getStatus());
		actualizada.setUpdatedAt(mesa.getUpdatedAt());
		try {
			return TablesEntityMapper.jpaToDominio(springtablesrepository.saveAndFlush(actualizada));
		}catch(DataIntegrityViolationException ex) {
			throw new TableYaRegistradaException("La mesa " + mesa.getTableNumber() + " ya está registrada");
		}
	}


	@Override
	public boolean existsByTenantIdAndTableNumber(UUID tenantId, int tableNumber) {
		return springtablesrepository.existsByTenantIdAndTableNumber(tenantId, tableNumber);
	}


	@Override
	public Optional<TableRestautant> findByTenantIdAndPublicId(UUID tenantId, UUID publicId) {
		return springtablesrepository.findByTenantIdAndPublicId(tenantId, publicId)
				.map(TablesEntityMapper::jpaToDominio);
	}


	@Override
	public List<TableRestautant> findByTenantId(UUID tenantId) {
		return springtablesrepository.findByTenantIdOrderByZoneAscTableNumberAsc(tenantId).stream()
				.map(TablesEntityMapper::jpaToDominio).toList();
	}


	@Override
	public List<TableRestautant> findByTenantIdAndStatus(UUID tenantId, TableStatus status) {
		return springtablesrepository.findByTenantIdAndStatusOrderByZoneAscTableNumberAsc(tenantId, status).stream()
				.map(TablesEntityMapper::jpaToDominio).toList();
	}


	// Borrado físico, solo de mesas sin pedidos (lo comprueba el servicio con tienePedidos).
	@Override
	public void deleteById(Long id) {
		log.debug("Eliminando la mesa {}", id);
		try {
			// flush lanza el DELETE aquí dentro: si entre la comprobación y el borrado se creó un pedido
			// en esta mesa, la clave foránea salta dentro del catch y no al confirmar la transacción (500).
			springtablesrepository.deleteById(id);
			springtablesrepository.flush();
		}catch(DataIntegrityViolationException ex) {
			throw new TableConPedidosException("La mesa tiene pedidos y no se puede eliminar");
		}
	}


	// Varias mesas en una consulta (las de un listado de pedidos), en vez de una por pedido (N+1).
	@Override
	public List<TableRestautant> findByIds(Collection<Long> ids) {
		if(ids.isEmpty()) {
			return List.of();
		}
		return springtablesrepository.findAllById(ids).stream().map(TablesEntityMapper::jpaToDominio).toList();
	}


	@Override
	public boolean tienePedidos(Long tableId) {
		return springtablesrepository.existsOrdersByTableId(tableId);
	}

}
