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

/**
 * Línea de un pedido: un producto, cuántas unidades y a qué precio. Un pedido tiene muchas líneas.
 * Sin tenant_id: a una línea solo se llega a través de su pedido, que ya filtra por tenant (como menu_item_modifiers).
 * Las restricciones reales viven en la migración de Flyway; aquí solo se documentan (ddl-auto=validate).
 */
@Entity
@Table(name = "order_items")
public class OrderItemEntity {

	@Id
	@Column(name = "orderitem_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orderitem_id_generator")
	@SequenceGenerator(name = "orderitem_id_generator", sequenceName = "orderitem_id_seq", allocationSize = 1)
	private Long orderitemId;

	// Para quitar o cambiar una línea concreta desde el TPV sin exponer el id interno.
	@Column(name = "public_id", nullable = false, unique = true, updatable = false)
	private UUID publicId;

	// Lado propietario de la relación: la clave foránea order_id vive en esta tabla.
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private OrderEntity order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "menuitems_id", nullable = false)
	private MenuItemEntity menuItem;

	// Foto del producto en el momento del pedido: si después cambia su nombre o su precio,
	// los pedidos y facturas antiguos siguen mostrando lo que se cobró.
	@Column(name = "product_name", nullable = false, length = 100)
	private String productName;

	@Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
	private BigDecimal unitPrice;

	@Column(nullable = false)
	private Integer quantity;

	// unit_price x quantity, guardado también como foto.
	@Column(name = "line_total", nullable = false, precision = 10, scale = 2)
	private BigDecimal lineTotal;

	// Indicaciones para cocina ("poco hecha", "sin sal"...).
	@Column(length = 255)
	private String notes;

	// Clave del envío del TPV que creó la línea: todas las líneas de un envío la comparten (no es UNIQUE).
	@Column(name = "idempotency_key", length = 100, updatable = false)
	private String idempotencyKey;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	public OrderItemEntity() {
		super();
	}

	public Long getOrderitemId() {
		return orderitemId;
	}

	public void setOrderitemId(Long orderitemId) {
		this.orderitemId = orderitemId;
	}

	public UUID getPublicId() {
		return publicId;
	}

	public void setPublicId(UUID publicId) {
		this.publicId = publicId;
	}

	public OrderEntity getOrder() {
		return order;
	}

	// Lo usan OrderEntity.addItem / removeItem para mantener los dos lados de la relación sincronizados.
	void setOrder(OrderEntity order) {
		this.order = order;
	}

	public MenuItemEntity getMenuItem() {
		return menuItem;
	}

	public void setMenuItem(MenuItemEntity menuItem) {
		this.menuItem = menuItem;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getLineTotal() {
		return lineTotal;
	}

	public void setLineTotal(BigDecimal lineTotal) {
		this.lineTotal = lineTotal;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getIdempotencyKey() {
		return idempotencyKey;
	}

	public void setIdempotencyKey(String idempotencyKey) {
		this.idempotencyKey = idempotencyKey;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	// Sin el pedido ni el producto completos (son LAZY): solo sus ids.
	@Override
	public String toString() {
		return "OrderItemEntity [orderitemId=" + orderitemId + ", publicId=" + publicId + ", orderId="
				+ (order != null ? order.getOrderId() : null) + ", menuItemId="
				+ (menuItem != null ? menuItem.getMenuitemsId() : null) + ", productName=" + productName
				+ ", unitPrice=" + unitPrice + ", quantity=" + quantity + ", lineTotal=" + lineTotal + "]";
	}

}
