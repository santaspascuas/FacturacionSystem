package com.facturacionAutonomo.product_service.infraestructure.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo de la modificación de un producto. El tenant llega en la cabecera X-Tenant-Id y el producto en la ruta.
 * active es Boolean y no boolean: si no viene en el JSON se rechaza, en vez de desactivar el producto sin querer.
 */
public class UpdateMenuItemRequest {

	@NotNull(message = "La categoría del producto es obligatoria")
	private UUID categoryPublicId;

	@NotBlank(message = "El nombre del producto es obligatorio")
	@Size(max = 100, message = "El nombre del producto no puede superar los 100 caracteres")
	private String name;

	private String description;

	@NotNull(message = "El precio del producto es obligatorio")
	@DecimalMin(value = "0.00", message = "El precio del producto no puede ser negativo")
	@Digits(integer = 8, fraction = 2, message = "El precio admite hasta 8 enteros y 2 decimales")
	private BigDecimal basePrice;

	@Size(max = 500, message = "La URL de la imagen no puede superar los 500 caracteres")
	private String imageUrl;

	@NotNull(message = "El estado activo del producto es obligatorio")
	private Boolean active;

	public UUID getCategoryPublicId() {
		return categoryPublicId;
	}

	public void setCategoryPublicId(UUID categoryPublicId) {
		this.categoryPublicId = categoryPublicId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getBasePrice() {
		return basePrice;
	}

	public void setBasePrice(BigDecimal basePrice) {
		this.basePrice = basePrice;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

}
