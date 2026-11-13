package com.facturacionAutonomo.product_service.infraestructure.persistence.mapper;

import com.facturacionAutonomo.product_service.domain.model.Modifier;
import com.facturacionAutonomo.product_service.infraestructure.persistence.entitys.ModifierEntity;

public class ModifierEntityMapper {

	// Solo tiene métodos estáticos: no se instancia (igual que los mappers de customer-service y auth-service).
	private ModifierEntityMapper() {}

	public static ModifierEntity dominioToJpa(Modifier modifier) {
		ModifierEntity crearjpa = new ModifierEntity();
		crearjpa.setTenantId(modifier.getTenantId());
		crearjpa.setPublicId(modifier.getPublicId());
		crearjpa.setName(modifier.getName());
		crearjpa.setModifierType(modifier.getModifierType());
		crearjpa.setPriceDelta(modifier.getPriceDelta());
		crearjpa.setActive(modifier.isActive());
		crearjpa.setCreatedAt(modifier.getCreatedAt());
		crearjpa.setUpdatedAt(modifier.getUpdatedAt());
		return crearjpa;
	}

	public static Modifier jpaToDominio(ModifierEntity modifier) {
		return Modifier.restoreData(modifier.getModificadorId(), modifier.getTenantId(),
				modifier.getPublicId(), modifier.getName(),
				modifier.getModifierType(), modifier.getPriceDelta(),
				modifier.getActive(), modifier.getCreatedAt(), modifier.getUpdatedAt());
	}

}
