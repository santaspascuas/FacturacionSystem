package com.facturacionAutonomo.product_service.infraestructure.persistence.entitys;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.OrderStatus;
import com.facturacionAutonomo.product_service.domain.model.OrderType;
import com.facturacionAutonomo.product_service.domain.model.SyncStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

/**
 * Pedido de un tenant (restaurante). El número de pedido y la clave de idempotencia son únicos dentro de cada tenant.
 * Las restricciones reales viven en la migración de Flyway; aquí solo se documentan (ddl-auto=validate).
 */
@Entity
@Table(name = "orders", uniqueConstraints = {
		@UniqueConstraint(name = "uk_order_tenant_number", columnNames = { "tenant_id", "order_number" }),
		@UniqueConstraint(name = "uk_orders_tenant_idempotency", columnNames = { "tenant_id", "idempotency_key" })
})
public class OrderEntity {

	@Id
	@Column(name = "order_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_id_generator")
	@SequenceGenerator(name = "order_id_generator", sequenceName = "order_id_seq", allocationSize = 1)
	private Long orderId;

	// public_id como en el resto de tablas: es el identificador que sale fuera del servicio.
	@Column(name = "public_id", nullable = false, unique = true, updatable = false)
	private UUID publicId;

	@Column(name = "tenant_id", nullable = false, updatable = false)
	private UUID tenantId;

	// Número correlativo dentro de cada tenant (uk_order_tenant_number), no en toda la BD: sin unique = true.
	@Column(name = "order_number", nullable = false)
	private Integer orderNumber;

	@Enumerated(EnumType.STRING)
	@Column(name = "order_type", nullable = false, length = 20)
	private OrderType orderType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private OrderStatus status;

	// Una mesa, una orden ABIERTA: lo garantiza el índice único parcial uk_orders_mesa_abierta de la V4
	// (JPA no puede expresar un UNIQUE con WHERE). En JPA es ManyToOne porque, con el historial,
	// una mesa acumula muchos pedidos cerrados.
	// Opcional: un pedido para LLEVAR no tiene mesa. Que sea del mismo tenant lo comprobará el servicio.
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "table_id")
	private TablesEntity table;

	// Una orden tiene muchas líneas. mappedBy: la clave foránea vive en order_items (OrderItemEntity.order).
	// cascade ALL + orphanRemoval: las líneas se guardan y se borran con su pedido; quitar una de la lista la borra.
	// @OrderBy: las líneas salen siempre en el orden en que se pidieron (el id de secuencia crece con cada línea).
	// Sin él, la BD no garantiza ningún orden.
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("orderitemId ASC")
	private List<OrderItemEntity> items = new ArrayList<>();

	// Número de personas del pedido. Opcional (para llevar no tiene sentido); si llega, al menos 1.
	@Column(name = "num_personas")
	private Integer numPersonas;

	// Importes guardados como foto del momento: si mañana cambia un precio, el pedido no cambia.
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal subtotal = BigDecimal.ZERO;

	// Tipo de IVA (%) aplicado: foto del momento del pedido. No cambia después de crearlo.
	@Column(name = "tax_rate", nullable = false, updatable = false, precision = 5, scale = 2)
	private BigDecimal taxRate;

	@Column(name = "tax_amount", nullable = false, precision = 10, scale = 2)
	private BigDecimal taxAmount = BigDecimal.ZERO;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal total = BigDecimal.ZERO;

	// Suma de los cobros (payments). CHECK de la V12: entre 0 y el total.
	@Column(name = "paid_amount", nullable = false, precision = 10, scale = 2)
	private BigDecimal paidAmount = BigDecimal.ZERO;

	// Cuándo quedó pagado del todo (paso a CIERRAYPAGA). Null mientras quede algo pendiente.
	@Column(name = "paid_at")
	private LocalDateTime paidAt;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	// Null hasta que cocina termina el pedido: un pedido recién creado todavía no se ha cocinado.
	@Column(name = "cooked_at")
	private LocalDateTime cookedAt;

	@Version
	private Long version;

	
	@Column(name = "idempotency_key", length = 100)
	private String idempotencyKey;

	// Hora del dispositivo cuando se creó el pedido (puede llegar más tarde si el TPV estaba sin conexión).
	@Column(name = "client_timestamp")
	private LocalDateTime clientTimestamp;

	@Enumerated(EnumType.STRING)
	@Column(name = "sync_status", nullable = false, length = 20)
	private SyncStatus syncStatus = SyncStatus.SYNCED;

	public OrderEntity() {
		super();
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
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

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public OrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public TablesEntity getTable() {
		return table;
	}

	public void setTable(TablesEntity table) {
		this.table = table;
	}

	// Lista privada, vista pública de solo lectura: nadie puede hacer getItems().add(...) y dejar la línea sin pedido.
	public List<OrderItemEntity> getItems() {
		return Collections.unmodifiableList(items);
	}

	// Mantiene sincronizados los dos lados de la relación: la línea entra en la lista y apunta a este pedido.
	public void addItem(OrderItemEntity item) {
		items.add(item);
		item.setOrder(this);
	}

	// Con orphanRemoval, la línea quitada se borra de la BD al guardar el pedido.
	public void removeItem(OrderItemEntity item) {
		items.remove(item);
		item.setOrder(null);
	}

	public Integer getNumPersonas() {
		return numPersonas;
	}

	public void setNumPersonas(Integer numPersonas) {
		this.numPersonas = numPersonas;
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

	public BigDecimal getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(BigDecimal paidAmount) {
		this.paidAmount = paidAmount;
	}

	public LocalDateTime getPaidAt() {
		return paidAt;
	}

	public void setPaidAt(LocalDateTime paidAt) {
		this.paidAt = paidAt;
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

	public LocalDateTime getCookedAt() {
		return cookedAt;
	}

	public void setCookedAt(LocalDateTime cookedAt) {
		this.cookedAt = cookedAt;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getIdempotencyKey() {
		return idempotencyKey;
	}

	public void setIdempotencyKey(String idempotencyKey) {
		this.idempotencyKey = idempotencyKey;
	}

	public LocalDateTime getClientTimestamp() {
		return clientTimestamp;
	}

	public void setClientTimestamp(LocalDateTime clientTimestamp) {
		this.clientTimestamp = clientTimestamp;
	}

	public SyncStatus getSyncStatus() {
		return syncStatus;
	}

	public void setSyncStatus(SyncStatus syncStatus) {
		this.syncStatus = syncStatus;
	}

	// Solo el id de la mesa y sin las líneas: imprimir relaciones LAZY dispararía su carga
	// (o LazyInitializationException fuera de una transacción). getTableId() de un proxy no lanza consulta.
	@Override
	public String toString() {
		return "OrderEntity [orderId=" + orderId + ", publicId=" + publicId + ", tenantId=" + tenantId
				+ ", orderNumber=" + orderNumber + ", orderType=" + orderType + ", status=" + status + ", tableId="
				+ (table != null ? table.getTableId() : null) + ", numPersonas=" + numPersonas + ", subtotal="
				+ subtotal + ", taxRate=" + taxRate + ", taxAmount=" + taxAmount + ", total=" + total + ", paidAmount=" + paidAmount + ", paidAt=" + paidAt
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", cookedAt=" + cookedAt + ", version="
				+ version + ", idempotencyKey=" + idempotencyKey + ", clientTimestamp=" + clientTimestamp
				+ ", syncStatus=" + syncStatus + "]";
	}

}
