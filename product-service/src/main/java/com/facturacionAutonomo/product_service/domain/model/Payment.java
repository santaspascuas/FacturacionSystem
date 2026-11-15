package com.facturacionAutonomo.product_service.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.exception.PaymentException;

/**
 * Un cobro de un pedido. Un pedido puede tener varios (cuenta dividida, parte en efectivo y parte con tarjeta).
 * Es un registro de caja: una vez creado no cambia, por eso todos sus campos son final y no tiene setters.
 * Lo que este cobro aplica al pedido es amount; en efectivo se guarda además lo entregado y el cambio.
 * Que el importe no supere lo que queda por pagar lo decide el pedido (Order), que es quien sabe su total.
 */
public class Payment {

	// DECIMAL(10,2) en payments: como mucho 8 cifras enteras y 2 decimales.
	private static final BigDecimal IMPORTE_MAXIMO = new BigDecimal("99999999.99");

	private final Long id;
	private final UUID tenantId;
	private final UUID publicId;
	private final Long orderId;
	private final BigDecimal amount;
	private final PaymentType paymentType;
	private final BigDecimal cashReceived;
	private final BigDecimal changeGiven;
	private final String idempotencyKey;
	private final LocalDateTime createdAt;


	private Payment(Long id, UUID tenantId, UUID publicId, Long orderId, BigDecimal amount, PaymentType paymentType,
			BigDecimal cashReceived, BigDecimal changeGiven, String idempotencyKey, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.tenantId = tenantId;
		this.publicId = publicId;
		this.orderId = orderId;
		this.amount = amount;
		this.paymentType = paymentType;
		this.cashReceived = cashReceived;
		this.changeGiven = changeGiven;
		this.idempotencyKey = idempotencyKey;
		this.createdAt = createdAt;
	}

	/**
	 * Crea un cobro nuevo. El TPV solo manda la cantidad: lo que se aplica al pedido lo calcula calculoPagoResultado
	 * según la forma de pago (efectivo: min(entregado, pendiente) y el resto es cambio; tarjeta: entera, sin pasarse).
	 * @param pendiente lo que le queda por pagar al pedido (Order.getImportePendiente()).
	 */
	public static Payment crearUnPayment(UUID tenantId, Long orderId, BigDecimal pendiente, PaymentType paymentType,
			BigDecimal cantidad, String idempotencyKey, LocalDateTime now) {
		confirmaPedido(orderId);
		tipoObligatorio(paymentType);
		BigDecimal entregado = validaImporte(cantidad);
		confirmaPendiente(pendiente);

		PaymentCalculationResult calculo = calculoPagoResultado(paymentType, entregado, pendiente);

		// id null: lo pone la secuencia al guardar. publicId nuevo: es el identificador que sale fuera.
		// La clave se normaliza igual que la del pedido: null, "" o "   " es sin clave (no se guarda texto vacío).
		return new Payment(null, tenantId, UUID.randomUUID(), orderId, calculo.aplicado(), paymentType,
				calculo.efectivo(), calculo.cambio(), Order.claveOpcional(idempotencyKey), now);
	}

	// Reconstruye un cobro leído de la BD. No valida: los datos ya pasaron las reglas al guardarse.
	public static Payment restoreData(Long id, UUID tenantId, UUID publicId, Long orderId, BigDecimal amount,
			PaymentType paymentType, BigDecimal cashReceived, BigDecimal changeGiven, String idempotencyKey,
			LocalDateTime createdAt) {
		return new Payment(id, tenantId, publicId, orderId, amount, paymentType, cashReceived, changeGiven,
				idempotencyKey, createdAt);
	}


	///--------Validaciones-----------------------------///

	private static Long confirmaPedido(Long orderId) {
		if(orderId == null) {
			throw new PaymentException("El cobro tiene que pertenecer a un pedido");
		}
		return orderId;
	}

	private static PaymentType tipoObligatorio(PaymentType paymentType) {
		if(paymentType == null) {
			throw new PaymentException("La forma de pago es obligatoria");
		}
		return paymentType;
	}

	private static BigDecimal validaImporte(BigDecimal amount) {
		if(amount == null || amount.signum() <= 0) {
			throw new PaymentException("El importe del cobro tiene que ser mayor que 0");
		}
		return formatoDeDinero(amount, "El importe del cobro");
	}

	// Coincide con DECIMAL(10,2): como mucho 2 decimales y 8 cifras enteras.
	private static BigDecimal formatoDeDinero(BigDecimal valor, String que) {
		if(valor.stripTrailingZeros().scale() > 2) {
			throw new PaymentException(que + " no puede tener más de 2 decimales");
		}
		if(valor.compareTo(IMPORTE_MAXIMO) > 0) {
			throw new PaymentException(que + " no puede superar " + IMPORTE_MAXIMO);
		}
		return valor;
	}

	private static void confirmaPendiente(BigDecimal pendiente) {
		if(pendiente == null || pendiente.signum() <= 0) {
			throw new PaymentException("El pedido no tiene nada pendiente de cobrar");
		}
	}

	// Qué parte de lo entregado se aplica al pedido, y cuánto efectivo y cambio hay.
	private static PaymentCalculationResult calculoPagoResultado(PaymentType paymentType, BigDecimal entregado,
			BigDecimal pendiente) {
		switch(paymentType) {
		case EFECTIVO:
			// Se aplica como mucho lo pendiente; lo que sobra es cambio. Lo entregado siempre cubre lo aplicado
			// (aplicado <= entregado), así que no hace falta comprobarlo.
			BigDecimal aplicado = entregado.min(pendiente);
			BigDecimal cambio = entregado.subtract(aplicado);
			return new PaymentCalculationResult(aplicado, entregado, cambio);
		case TARJETA:
			// Con tarjeta se cobra lo que se pasa por el datáfono: exacto, nunca más de lo pendiente.
			if(entregado.compareTo(pendiente) > 0) {
				throw new PaymentException("Con " + paymentType + " no se puede cobrar más de lo pendiente (" + pendiente + ")");
			}
			// Sin efectivo ni cambio: el CHECK ck_payments_efectivo exige que vayan a null.
			return new PaymentCalculationResult(entregado, null, null);
		default:
			throw new PaymentException("Tipo de pago no soportado: " + paymentType);
		}
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
	public Long getOrderId() {
		return orderId;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public PaymentType getPaymentType() {
		return paymentType;
	}
	public BigDecimal getCashReceived() {
		return cashReceived;
	}
	public BigDecimal getChangeGiven() {
		return changeGiven;
	}
	public String getIdempotencyKey() {
		return idempotencyKey;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	@Override
	public String toString() {
		return "Payment [id=" + id + ", tenantId=" + tenantId + ", publicId=" + publicId + ", orderId=" + orderId
				+ ", amount=" + amount + ", paymentType=" + paymentType + ", cashReceived=" + cashReceived
				+ ", changeGiven=" + changeGiven + ", idempotencyKey=" + idempotencyKey + ", createdAt=" + createdAt
				+ "]";
	}

}
