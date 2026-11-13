package com.facturacionAutonomo.product_service.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.exception.ModifierException;

/**
 * Modificador de la carta de un tenant (restaurante), p. ej. "Sin cebolla" o "Extra de queso".
 * El nombre solo es único dentro de cada tenant. priceDelta puede ser negativo.
 */
public class Modifier {
	private final Long id;
	private final UUID tenantId;
	private final UUID publicId;
	private String name;
	private ModifierType modifierType;
	private BigDecimal priceDelta;
	private boolean active;
	private final LocalDateTime createdAt;
	private LocalDateTime updatedAt;


	private Modifier(Long id, UUID tenantId, UUID publicId, String name, ModifierType modifierType,
			BigDecimal priceDelta, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.id = id;
		this.tenantId = tenantId;
		this.publicId = publicId;
		this.name = name;
		this.modifierType = modifierType;
		this.priceDelta = priceDelta;
		this.active = active;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}


	// Un modificador nuevo nace activo y con su UUID público, que es el que se expone fuera del servicio.
	public static Modifier crearModifier(UUID tenantId, String name, ModifierType modifierType, BigDecimal priceDelta,
			LocalDateTime now) {
		return new Modifier(null, tenantId, UUID.randomUUID(), normalizarNombre(name), tipoObligatorio(modifierType),
				precioObligatorio(priceDelta), true, now, now);
	}

	public static Modifier restoreData(Long id, UUID tenantId, UUID publicId, String name, ModifierType modifierType,
			BigDecimal priceDelta, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
		return new Modifier(id, tenantId, publicId, name, modifierType, priceDelta, active, createdAt, updatedAt);
	}

	public void updateDetails(String name, ModifierType modifierType, boolean active, LocalDateTime updatedAt) {
		this.name = normalizarNombre(name);
		this.modifierType = tipoObligatorio(modifierType);
		this.active = active;
		this.updatedAt = updatedAt;
	}

	// Como con el precio de los productos, solo el admin puede cambiarlo: se separa de updateDetails.
	// No se comprueba el signo: quitar un ingrediente puede abaratar el producto.
	public void changePrice(BigDecimal priceDelta, LocalDateTime updatedAt) {
		this.priceDelta = precioObligatorio(priceDelta);
		this.updatedAt = updatedAt;
	}

	// Lo usan también los servicios para comprobar que el nombre está libre con el mismo valor que se guardará.
	public static String normalizarNombre(String name) {
		if(name == null || name.isBlank()) {
			throw new ModifierException("El nombre del modificador es obligatorio");
		}
		return name.trim();
	}

	private static ModifierType tipoObligatorio(ModifierType modifierType) {
		if(modifierType == null) {
			throw new ModifierException("El tipo del modificador es obligatorio");
		}
		return modifierType;
	}

	private static BigDecimal precioObligatorio(BigDecimal priceDelta) {
		if(priceDelta == null) {
			throw new ModifierException("El precio del modificador es obligatorio");
		}
		return priceDelta;
	}

	// Borrado lógico: deja de ofrecerse, pero las líneas de pedido antiguas lo siguen referenciando.
	public void eliminar(LocalDateTime updatedAt) {
		this.active = false;
		this.updatedAt = updatedAt;
	}


	public Long getId() {
		return id;
	}
	public UUID getTenantId() {
		return tenantId;
	}
	public UUID getPublicId() {
		return publicId;
	}
	public String getName() {
		return name;
	}
	public ModifierType getModifierType() {
		return modifierType;
	}
	public BigDecimal getPriceDelta() {
		return priceDelta;
	}
	public boolean isActive() {
		return active;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	@Override
	public String toString() {
		return "Modifier [id=" + id + ", tenantId=" + tenantId + ", publicId=" + publicId + ", name=" + name
				+ ", modifierType=" + modifierType + ", priceDelta=" + priceDelta + ", active=" + active
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}

}
