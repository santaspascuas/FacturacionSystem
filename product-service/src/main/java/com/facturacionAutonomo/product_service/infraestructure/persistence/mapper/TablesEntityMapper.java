package com.facturacionAutonomo.product_service.infraestructure.persistence.mapper;

import com.facturacionAutonomo.product_service.domain.model.TableRestautant;
import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.TablesEntity;

// Puente entre la entidad JPA (con setters, la rellena Hibernate) y el dominio (sin setters, se crea con restoreData).
public class TablesEntityMapper {

	// Solo tiene métodos estáticos: no se instancia (igual que los mappers de customer-service y auth-service).
	private TablesEntityMapper() {}

	// Para dar de alta: el id no se copia, lo pone la secuencia al guardar. El version tampoco (Hibernate).
	public static TablesEntity dominioToJpa(TableRestautant mesa) {
		TablesEntity crearjpa = new TablesEntity();
		crearjpa.setTenantId(mesa.getTenantId());
		crearjpa.setPublicId(mesa.getPublicId());
		crearjpa.setTableNumber(mesa.getTableNumber());
		crearjpa.setCapacity(mesa.getCapacity());
		crearjpa.setZone(mesa.getZone());
		crearjpa.setStatus(mesa.getStatus());
		crearjpa.setCreatedAt(mesa.getCreatedAt());
		crearjpa.setUpdatedAt(mesa.getUpdatedAt());
		return crearjpa;
	}

	public static TableRestautant jpaToDominio(TablesEntity entidad) {
		return TableRestautant.restoreData(entidad.getTableId(), entidad.getTenantId(), entidad.getPublicId(),
				entidad.getTableNumber(), entidad.getCapacity(), entidad.getZone(), entidad.getStatus(),
				entidad.getCreatedAt(), entidad.getUpdatedAt());
	}

}
