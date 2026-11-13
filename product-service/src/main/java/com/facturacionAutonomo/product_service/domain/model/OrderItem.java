package com.facturacionAutonomo.product_service.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.exception.OrderException;

/**
 * Línea de un pedido: un producto, cuántas unidades y a qué precio.
 * No existe fuera de su pedido: se crea aquí, pero solo entra en un pedido con Order.addItem.
 * productName y unitPrice son una foto del producto en el momento del pedido (para la factura).
 */
public class OrderItem {
	private final Long id;
	private final UUID publicId;
	private final Long menuItemId;
	private final String productName;
	private final BigDecimal unitPrice;
	private int quantity;
	private BigDecimal lineTotal;
	private String notes;
	private final LocalDateTime createdAt;


	private OrderItem(Long id, UUID publicId, Long menuItemId, String productName, BigDecimal unitPrice, int quantity,
			BigDecimal lineTotal, String notes, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.publicId = publicId;
		this.menuItemId = menuItemId;
		this.productName = productName;
		this.unitPrice = unitPrice;
		this.quantity = quantity;
		this.lineTotal = lineTotal;
		this.notes = notes;
		this.createdAt = createdAt;
	}


	// El nombre y el precio se copian del producto (MenuItem) al crear la línea: después ya no cambian.
	public static OrderItem crearOrderItem(Long menuItemId, String productName, BigDecimal unitPrice, Integer quantity,
			String notes, LocalDateTime now) {
		if(menuItemId == null) {
			throw new OrderException("El producto de la línea es obligatorio");
		}
		int cantidad = cantidadValida(quantity);
		BigDecimal precio = precioValido(unitPrice);
		return new OrderItem(null, UUID.randomUUID(), menuItemId, nombreObligatorio(productName), precio, cantidad,
				calcularTotal(precio, cantidad), notasOpcionales(notes), now);
	}

	public static OrderItem restoreData(Long id, UUID publicId, Long menuItemId, String productName,
			BigDecimal unitPrice, int quantity, BigDecimal lineTotal, String notes, LocalDateTime createdAt) {
		return new OrderItem(id, publicId, menuItemId, productName, unitPrice, quantity, lineTotal, notes, createdAt);
	}


	// ---------- Validaciones compartidas ----------

	// Coincide con el CHECK (quantity > 0) de order_items.
	public static int cantidadValida(Integer quantity) {
		if(quantity == null || quantity < 1) {
			throw new OrderException("La cantidad tiene que ser al menos 1");
		}
		return quantity;
	}

	public static BigDecimal precioValido(BigDecimal unitPrice) {
		if(unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
			throw new OrderException("El precio de la línea es obligatorio y no puede ser negativo");
		}
		return unitPrice;
	}

	private static String nombreObligatorio(String productName) {
		if(productName == null || productName.isBlank()) {
			throw new OrderException("El nombre del producto de la línea es obligatorio");
		}
		return productName.trim();
	}

	// "" o "   " se guardan como null.
	private static String notasOpcionales(String notes) {
		return (notes == null || notes.isBlank()) ? null : notes.trim();
	}

	private static BigDecimal calcularTotal(BigDecimal unitPrice, int quantity) {
		return unitPrice.multiply(BigDecimal.valueOf(quantity));
	}


	// ---------- Comportamiento ----------
	// Son de paquete (sin public): una línea solo cambia a través de su pedido, que recalcula sus totales.

	void cambiarCantidad(Integer quantity) {
		this.quantity = cantidadValida(quantity);
		this.lineTotal = calcularTotal(unitPrice, this.quantity);
	}

	void cambiarNotas(String notes) {
		this.notes = notasOpcionales(notes);
	}


	public Long getId() {
		return id;
	}
	public UUID getPublicId() {
		return publicId;
	}
	public Long getMenuItemId() {
		return menuItemId;
	}
	public String getProductName() {
		return productName;
	}
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}
	public int getQuantity() {
		return quantity;
	}
	public BigDecimal getLineTotal() {
		return lineTotal;
	}
	public String getNotes() {
		return notes;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	@Override
	public String toString() {
		return "OrderItem [id=" + id + ", publicId=" + publicId + ", menuItemId=" + menuItemId + ", productName="
				+ productName + ", unitPrice=" + unitPrice + ", quantity=" + quantity + ", lineTotal=" + lineTotal
				+ "]";
	}

}
