package com.facturacionAutonomo.product_service.infraestructure.persistence.entitys;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.model.PaymentType;

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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Un cobro de un pedido. Un pedido puede tener varios (cuenta dividida, parte en efectivo y parte con tarjeta).
 * Es un registro de caja: no se modifica nunca, por eso todas sus columnas son updatable = false y no tiene
 * updated_at ni @Version. La concurrencia la controla el @Version del pedido al sumar lo cobrado.
 * La clave de idempotencia es única dentro de cada tenant: un reintento del TPV no cobra dos veces.
 * Las restricciones reales viven en la migración V11; aquí solo se documentan (ddl-auto=validate).
 */
@Entity
@Table(name = "payments", uniqueConstraints =
		@UniqueConstraint(name = "uk_payments_tenant_idempotency", columnNames = { "tenant_id", "idempotency_key" }))
public class PaymentEntity {

	@Id
	@Column(name = "payment_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_id_generator")
	@SequenceGenerator(name = "payment_id_generator", sequenceName = "payment_id_seq", allocationSize = 1)
	private Long paymentId;

	// public_id como en el resto de tablas: es el identificador que sale fuera del servicio.
	@Column(name = "public_id", nullable = false, unique = true, updatable = false)
	private UUID publicId;

	@Column(name = "tenant_id", nullable = false, updatable = false)
	private UUID tenantId;

	// Solo en este sentido (sin lista de pagos en OrderEntity): con dos listas en el pedido (items y payments),
	// cargarlas a la vez daría MultipleBagFetchException. LAZY: leer un pago no carga su pedido.
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false, updatable = false)
	private OrderEntity order;

	// Lo que este cobro aplica al pedido. Sin valor por defecto: un importe sin rellenar tiene que fallar, no ser 0.
	@Column(nullable = false, updatable = false, precision = 10, scale = 2)
	private BigDecimal amount;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_type", nullable = false, updatable = false, length = 20)
	private PaymentType paymentType;

	// Solo en efectivo: lo que entregó el cliente y el cambio devuelto (cash_received - amount).
	// Con tarjeta van a null. Lo garantiza el CHECK ck_payments_efectivo de la V11.
	@Column(name = "cash_received", updatable = false, precision = 10, scale = 2)
	private BigDecimal cashReceived;

	@Column(name = "change_given", updatable = false, precision = 10, scale = 2)
	private BigDecimal changeGiven;

	@Column(name = "idempotency_key", length = 100, updatable = false)
	private String idempotencyKey;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	public PaymentEntity() {
		super();
	}

	public Long getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Long paymentId) {
		this.paymentId = paymentId;
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

	public OrderEntity getOrder() {
		return order;
	}

	public void setOrder(OrderEntity order) {
		this.order = order;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public PaymentType getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	public BigDecimal getCashReceived() {
		return cashReceived;
	}

	public void setCashReceived(BigDecimal cashReceived) {
		this.cashReceived = cashReceived;
	}

	public BigDecimal getChangeGiven() {
		return changeGiven;
	}

	public void setChangeGiven(BigDecimal changeGiven) {
		this.changeGiven = changeGiven;
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

	// Solo el id del pedido: imprimir la relación LAZY dispararía su carga
	// (o LazyInitializationException fuera de una transacción). getOrderId() de un proxy no lanza consulta.
	@Override
	public String toString() {
		return "PaymentEntity [paymentId=" + paymentId + ", publicId=" + publicId + ", tenantId=" + tenantId
				+ ", orderId=" + (order != null ? order.getOrderId() : null) + ", amount=" + amount
				+ ", paymentType=" + paymentType + ", cashReceived=" + cashReceived + ", changeGiven=" + changeGiven
				+ ", idempotencyKey=" + idempotencyKey + ", createdAt=" + createdAt + "]";
	}

}
