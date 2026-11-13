package com.facturacionAutonomo.product_service.infraestructure.persistence.entitys;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "menu_items")
public class MenuItemEntity {
	
	@Id
	@Column(name = "menuitems_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "menuitems_id_generator")
	@SequenceGenerator(name = "menuitems_id_generator", sequenceName = "menuitems_id_seq", allocationSize = 1)
	private Long menuitemsId;
	
	@Column(name = "public_id", nullable = false, unique = true, updatable = false)
	private UUID publicId;

	@Column(name = "tenant_id", nullable = false, updatable = false)
	private UUID tenantId;

    @Column(nullable = false, length = 100)
    private String name;
	
    @Column(columnDefinition = "TEXT")
    private String description;
	
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private MenuCategoryEntity category;
    
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @Column(length = 500)
    private String imageUrl;
    
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;
	
	@Version
	private Long version;

	public MenuItemEntity() {
		super();
	}

	public Long getMenuitemsId() {
		return menuitemsId;
	}

	public void setMenuitemsId(Long menuitemsId) {
		this.menuitemsId = menuitemsId;
	}

	public UUID getPublicId() {
		return publicId;
	}

	public void setPublicId(UUID publicId) {
		this.publicId = publicId;
	}

	public UUID getTenantId() {
		return tenantId;
	}

	public void setTenantId(UUID tenantId) {
		this.tenantId = tenantId;
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

	public MenuCategoryEntity getCategory() {
		return category;
	}

	public void setCategory(MenuCategoryEntity category) {
		this.category = category;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
	
	
	
	
	
	
	
}
