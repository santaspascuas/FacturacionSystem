package com.facturacionAutonomo.product_service.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.exception.MenuItemException;

/**
 * Producto de la carta de un tenant (restaurante). Pertenece a una categoría del mismo tenant.
 */
public class MenuItem {
	// final: el id solo entra por crearMenuItem (null, aún sin guardar) o por restoreData (el de la BD).
	private final Long id;
	private final UUID tenantId;
	private final UUID publicId;
	private Long categoryId;
	private String name;
	private String description;
	private BigDecimal basePrice;
	private boolean active;
	private String imageUrl;
	private final LocalDateTime createdAt;
	private LocalDateTime updatedAt;


	private MenuItem(Long id, UUID tenantId, UUID publicId, Long categoryId, String name, String description,
			BigDecimal basePrice, boolean active, String imageUrl, LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.id = id;
		this.tenantId = tenantId;
		this.publicId = publicId;
		this.categoryId = categoryId;
		this.name = name;
		this.description = description;
		this.basePrice = basePrice;
		this.active = active;
		this.imageUrl = imageUrl;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}


	// Un producto nuevo nace activo y con su UUID público, que es el que se expone fuera del servicio.
	public static MenuItem crearMenuItem(UUID tenantId, Long categoryId, String name, String description,
			BigDecimal basePrice, String imageUrl, LocalDateTime now) {
		return new MenuItem(null, tenantId, UUID.randomUUID(), categoryId, name, description, basePrice, true, imageUrl,
				now, now);
	}

	public static MenuItem restoreData(Long id, UUID tenantId, UUID publicId, Long categoryId, String name,
			String description, BigDecimal basePrice, boolean active, String imageUrl, LocalDateTime createdAt,
			LocalDateTime updatedAt) {
		return new MenuItem(id, tenantId, publicId, categoryId, name, description, basePrice, active, imageUrl,
				createdAt, updatedAt);
	}

	// Validaciones que comparten el alta y la modificación. Son estáticas para que el servicio las llame
	// antes de buscar la categoría, como hacía hasta ahora.
	public static String normalizarNombre(String name) {
		if(name == null || name.isBlank()) {
			throw new MenuItemException("El nombre del producto es obligatorio");
		}
		return name.trim();
	}

	public static BigDecimal precioValido(BigDecimal basePrice) {
		if(basePrice == null || basePrice.compareTo(BigDecimal.ZERO) < 0) {
			throw new MenuItemException("El precio del producto es obligatorio y no puede ser negativo");
		}
		return basePrice;
	}

	// "" o "   " se guardan como null, no como texto vacío (descripción e imagen).
	public static String textoOpcional(String texto) {
		return (texto == null || texto.isBlank()) ? null : texto.trim();
	}

	public void updateDetails(Long categoryId, String name, String description, String imageUrl, boolean active,
			LocalDateTime updatedAt) {
		this.categoryId = categoryId;
		this.name = name;
		this.description = description;
		this.imageUrl = imageUrl;
		this.active = active;
		this.updatedAt = updatedAt;
	}
	
	public void changePrice(BigDecimal basePrice, LocalDateTime updatedAt) {
		this.basePrice = basePrice;
		this.updatedAt = updatedAt;
	}

	// Borrado lógico: el producto deja de salir en la carta, pero los pedidos y facturas antiguos lo siguen referenciando.
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
	public Long getCategoryId() {
		return categoryId;
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public BigDecimal getBasePrice() {
		return basePrice;
	}
	public boolean isActive() {
		return active;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	@Override
	public String toString() {
		return "MenuItem [id=" + id + ", tenantId=" + tenantId + ", publicId=" + publicId + ", categoryId="
				+ categoryId + ", name=" + name + ", basePrice=" + basePrice + ", active=" + active + ", createdAt="
				+ createdAt + ", updatedAt=" + updatedAt + "]";
	}

}
