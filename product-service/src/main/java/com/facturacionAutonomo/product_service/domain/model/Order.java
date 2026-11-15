package com.facturacionAutonomo.product_service.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.facturacionAutonomo.product_service.domain.exception.OrderException;



/**
 * Pedido de un tenant (restaurante). Es la raíz del agregado: las líneas (OrderItem) viven en una lista privada
 * y solo cambian con los métodos de Order, que recalculan los totales. Así el total siempre cuadra con las líneas.
 */
public class Order {

	// Un pedido pagado (CIERRAYPAGA) o cancelado está cerrado: no se modifica y no cuenta como el pedido abierto
	// de su mesa. CIERRAYPAGA es además el que cuenta para la facturación (se cierra y se paga el total).
	// Las consultas del repositorio usan esta misma lista para que la regla no se repita.
	public static final Set<OrderStatus> ESTADOS_CERRADOS = Set.of(OrderStatus.CIERRAYPAGA, OrderStatus.CANCELADO);

	// Los abiertos son todos los demás. Se calcula a partir de los cerrados: si mañana se añade un estado nuevo
	// al enum, cuenta como abierto sin tener que acordarse de tocar esta lista.
	public static final Set<OrderStatus> ESTADOS_ABIERTOS = Collections
			.unmodifiableSet(EnumSet.complementOf(EnumSet.copyOf(ESTADOS_CERRADOS)));

	private static final BigDecimal CIEN = BigDecimal.valueOf(100);

	private final Long id;
	private final UUID tenantId;
	private final UUID publicId;
	private final Integer orderNumber;
	private final OrderType orderType;
	private OrderStatus status;
	private final Long tableId;
	private Integer numPersonas;
	private final List<OrderItem> items;
	private BigDecimal subtotal;
	private final BigDecimal taxRate;
	private BigDecimal taxAmount;
	private BigDecimal total;
	// Suma de lo cobrado (los Payment del pedido). Cuando llega al total, el pedido queda pagado (CIERRAYPAGA).
	// Vive en el pedido para que él mismo pueda aplicar sus reglas: no cobrar de más, no bajar el total por debajo
	// de lo cobrado y no cancelar con dinero cobrado. Cada cobro lo cambia, así que el @Version frena cobros a la vez.
	private BigDecimal paidAmount;
	// Cuándo quedó pagado del todo. Null mientras quede algo pendiente.
	private LocalDateTime paidAt;
	private final LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime cookedAt;
	private final String idempotencyKey;
	private final LocalDateTime clientTimestamp;
	private SyncStatus syncStatus;


	private Order(Long id, UUID tenantId, UUID publicId, Integer orderNumber, OrderType orderType, OrderStatus status,
			Long tableId, Integer numPersonas, List<OrderItem> items, BigDecimal subtotal, BigDecimal taxRate,
			BigDecimal taxAmount, BigDecimal total, BigDecimal paidAmount, LocalDateTime paidAt,
			LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime cookedAt, String idempotencyKey,
			LocalDateTime clientTimestamp, SyncStatus syncStatus) {
		super();
		this.id = id;
		this.tenantId = tenantId;
		this.publicId = publicId;
		this.orderNumber = orderNumber;
		this.orderType = orderType;
		this.status = status;
		this.tableId = tableId;
		this.numPersonas = numPersonas;
		// Copia propia: quien pasó la lista no puede modificar las líneas del pedido desde fuera.
		this.items = new ArrayList<>(items);
		this.subtotal = subtotal;
		this.taxRate = taxRate;
		this.taxAmount = taxAmount;
		this.total = total;
		this.paidAmount = paidAmount;
		this.paidAt = paidAt;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.cookedAt = cookedAt;
		this.idempotencyKey = idempotencyKey;
		this.clientTimestamp = clientTimestamp;
		this.syncStatus = syncStatus;
	}


	// Un pedido nuevo nace ENVIADO, sin líneas, con los importes a 0 y con su UUID público.
	// taxRate es el tipo de IVA vigente para el tenant: el pedido no sabe de dónde sale, solo lo guarda y lo aplica.
	public static Order crearNuevaOrder(UUID tenantId, Integer orderNumber, OrderType orderType, Long tableId,
			Integer numPersonas, BigDecimal taxRate, String idempotencyKey, LocalDateTime clientTimestamp,
			LocalDateTime now) {
		validarMesaSegunTipo(tipoObligatorio(orderType), tableId);
		return new Order(null, tenantId, UUID.randomUUID(), numeroValido(orderNumber), orderType, OrderStatus.ENVIADO,
				tableId, personasValidas(numPersonas), List.of(), BigDecimal.ZERO, tipoImpositivoValido(taxRate),
				BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, null, now, now, null, claveOpcional(idempotencyKey),
				clientTimestamp, SyncStatus.SYNCED);
	}

	// Reconstruye un pedido leído de la BD, con sus líneas. No valida: los datos ya pasaron las reglas al guardarse.
	public static Order restoreData(Long id, UUID tenantId, UUID publicId, Integer orderNumber, OrderType orderType,
			OrderStatus status, Long tableId, Integer numPersonas, List<OrderItem> items, BigDecimal subtotal,
			BigDecimal taxRate, BigDecimal taxAmount, BigDecimal total, BigDecimal paidAmount, LocalDateTime paidAt,
			LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime cookedAt, String idempotencyKey,
			LocalDateTime clientTimestamp, SyncStatus syncStatus) {
		return new Order(id, tenantId, publicId, orderNumber, orderType, status, tableId, numPersonas, items, subtotal,
				taxRate, taxAmount, total, paidAmount, paidAt, createdAt, updatedAt, cookedAt, idempotencyKey,
				clientTimestamp, syncStatus);
	}


	// ---------- Validaciones compartidas ----------

	// Coincide con el CHECK (order_number > 0) de orders.
	public static int numeroValido(Integer orderNumber) {
		if(orderNumber == null || orderNumber < 1) {
			throw new OrderException("El número de pedido tiene que ser al menos 1");
		}
		return orderNumber;
	}

	public static OrderType tipoObligatorio(OrderType orderType) {
		if(orderType == null) {
			throw new OrderException("El tipo de pedido es obligatorio");
		}
		return orderType;
	}

	// Número de personas del pedido: opcional (en un pedido para llevar no tiene sentido), pero si llega, al menos 1.
	// Coincide con el CHECK (num_personas > 0) de orders.
	public static Integer personasValidas(Integer numPersonas) {
		if(numPersonas != null && numPersonas < 1) {
			throw new OrderException("El número de personas tiene que ser al menos 1");
		}
		return numPersonas;
	}

	public static OrderStatus estadoObligatorio(OrderStatus status) {
		if(status == null) {
			throw new OrderException("El estado del pedido es obligatorio");
		}
		return status;
	}

	// Coincide con el CHECK (tax_rate >= 0 AND tax_rate < 100) de orders. 0 es válido (operación exenta).
	public static BigDecimal tipoImpositivoValido(BigDecimal taxRate) {
		if(taxRate == null || taxRate.signum() < 0 || taxRate.compareTo(CIEN) >= 0) {
			throw new OrderException("El tipo de IVA tiene que estar entre 0 y 100");
		}
		return taxRate;
	}

	// Clave de idempotencia del TPV (del pedido o de un envío de líneas). Opcional: null, "" o "   " es sin clave.
	// Coincide con VARCHAR(100) de orders.idempotency_key y order_items.idempotency_key.
	public static String claveOpcional(String idempotencyKey) {
		if(idempotencyKey == null || idempotencyKey.isBlank()) {
			return null;
		}
		String clave = idempotencyKey.trim();
		if(clave.length() > 100) {
			throw new OrderException("La clave de idempotencia no puede superar los 100 caracteres");
		}
		return clave;
	}

	// Comer aquí va en una mesa; para llevar, no.
	private static void validarMesaSegunTipo(OrderType orderType, Long tableId) {
		if(orderType == OrderType.COMERAQUI && tableId == null) {
			throw new OrderException("Un pedido para comer aquí necesita una mesa");
		}
		if(orderType == OrderType.LLEVAR && tableId != null) {
			throw new OrderException("Un pedido para llevar no tiene mesa");
		}
	}


	// ---------- Líneas (la única forma de cambiar la lista) ----------

	// Un plato nuevo hay que cocinarlo: el pedido vuelve a ENVIADO aunque ya estuviera LISTO.
	public void addItem(OrderItem item, LocalDateTime now) {
		comprobarAbierta();
		if(item == null) {
			throw new OrderException("La línea es obligatoria");
		}
		items.add(item);
		mandarACocina();
		recalcularTotales(now);
	}

	// Si la cantidad sube, también hay más que cocinar: vuelve a ENVIADO. Si baja, el estado no cambia.
	public void cambiarCantidad(UUID itemPublicId, Integer quantity, LocalDateTime now) {
		comprobarAbierta();
		OrderItem item = buscarItem(itemPublicId);
		int cantidad = OrderItem.cantidadValida(quantity);
		// Antes de tocar nada: cómo quedaría el total con la cantidad nueva.
		comprobarTotalCubreLoCobrado(total.subtract(item.getLineTotal())
				.add(item.getUnitPrice().multiply(BigDecimal.valueOf(cantidad))));
		if(item.cambiarCantidad(cantidad)) {
			mandarACocina();
		}
		recalcularTotales(now);
	}

	// True si el pedido ya tiene líneas de ese envío del TPV: es un reintento y no hay que añadirlas otra vez.
	public boolean envioYaAplicado(String idempotencyKey) {
		String clave = claveOpcional(idempotencyKey);
		return items.stream().anyMatch(item -> item.esDelEnvio(clave));
	}

	public void cambiarNotas(UUID itemPublicId, String notes, LocalDateTime now) {
		comprobarAbierta();
		buscarItem(itemPublicId).cambiarNotas(notes);
		this.updatedAt = now;
	}

	// Un pedido no se queda sin líneas (el alta exige al menos una): para anularlo entero está CANCELADO.
	public void quitarItem(UUID itemPublicId, LocalDateTime now) {
		comprobarAbierta();
		OrderItem item = buscarItem(itemPublicId);
		if(items.size() == 1) {
			throw new OrderException("No se puede quitar la última línea del pedido: anúlalo con el estado CANCELADO");
		}
		comprobarTotalCubreLoCobrado(total.subtract(item.getLineTotal()));
		items.remove(item);
		recalcularTotales(now);
	}

	// Vista pública de solo lectura: getItems().add(...) lanza UnsupportedOperationException.
	public List<OrderItem> getItems() {
		return List.copyOf(items);
	}


	// ---------- Datos del pedido ----------

	// Lo único del pedido que se puede cambiar después de abrirlo: el tipo y la mesa quedan fijos.
	public void cambiarPersonas(Integer numPersonas, LocalDateTime now) {
		comprobarAbierta();
		this.numPersonas = personasValidas(numPersonas);
		this.updatedAt = now;
	}


	// ---------- Estado ----------

	// Estados de cocina y CANCELADO. CIERRAYPAGA no se pone a mano: lo pone registrarCobro cuando está todo cobrado.
	public void cambiarEstado(OrderStatus status, LocalDateTime now) {
		comprobarAbierta();
		estadoObligatorio(status);
		if(status == OrderStatus.CIERRAYPAGA) {
			throw new OrderException("Un pedido solo se cierra y paga al cobrarlo entero");
		}
		if(status == OrderStatus.CANCELADO && paidAmount.signum() > 0) {
			throw new OrderException("No se puede cancelar el pedido " + orderNumber + ": ya tiene " + paidAmount
					+ " cobrados");
		}
		this.status = status;
		if(status == OrderStatus.LISTO) {
			this.cookedAt = now;
		}
		this.updatedAt = now;
	}


	// ---------- Cobro ----------

	// Lo que queda por pagar. 0 cuando el pedido está pagado del todo.
	public BigDecimal getImportePendiente() {
		return total.subtract(paidAmount);
	}

	/**
	 * Suma un cobro al pedido. El importe ya viene calculado por Payment.crearUnPayment, pero el pedido lo vuelve a
	 * comprobar: es quien sabe lo pendiente de verdad (y lo que se valida aquí es lo que protege el @Version).
	 * Si con este cobro queda todo pagado, el pedido pasa a CIERRAYPAGA y se cierra.
	 */
	public void registrarCobro(BigDecimal importe, LocalDateTime now) {
		comprobarAbierta();
		if(items.isEmpty()) {
			throw new OrderException("No se puede cobrar un pedido sin líneas");
		}
		if(importe == null || importe.signum() <= 0) {
			throw new OrderException("El importe del cobro tiene que ser mayor que 0");
		}
		BigDecimal pendiente = getImportePendiente();
		if(importe.compareTo(pendiente) > 0) {
			throw new OrderException("El cobro (" + importe + ") supera lo pendiente del pedido (" + pendiente + ")");
		}
		this.paidAmount = paidAmount.add(importe);
		this.updatedAt = now;
		// compareTo y no equals: con BigDecimal, 13.8 y 13.80 no son equals (distinta escala) pero valen lo mismo.
		if(paidAmount.compareTo(total) == 0) {
			this.status = OrderStatus.CIERRAYPAGA;
			this.paidAt = now;
		}
	}

	// Abierto: todavía se puede modificar. Pagado o cancelado, el pedido queda cerrado (historial de facturación).
	public boolean estaAbierta() {
		return !ESTADOS_CERRADOS.contains(status);
	}


	// ---------- Internos ----------

	private void comprobarAbierta() {
		if(!estaAbierta()) {
			throw new OrderException("El pedido " + orderNumber + " está cerrado y no se puede modificar");
		}
	}

	// Con algo ya cobrado, el total no puede quedar por debajo (habría que devolver dinero). Se comprueba ANTES de
	// cambiar nada, con el total que quedaría, para que el pedido nunca se quede en un estado imposible.
	// El total es siempre la suma exacta de las líneas (base + cuota = importe con IVA), así que basta con
	// restar el importe de la línea que cambia y sumar el nuevo.
	private void comprobarTotalCubreLoCobrado(BigDecimal totalQueQuedaria) {
		if(totalQueQuedaria.compareTo(paidAmount) < 0) {
			throw new OrderException("El pedido ya tiene " + paidAmount + " cobrados: el total no puede quedar en "
					+ totalQueQuedaria);
		}
	}

	// Vuelve a la cola de cocina: ENVIADO y sin hora de terminado (se pondrá otra vez al pasar a LISTO).
	private void mandarACocina() {
		this.status = OrderStatus.ENVIADO;
		this.cookedAt = null;
	}

	private OrderItem buscarItem(UUID itemPublicId) {
		if(itemPublicId == null) {
			throw new OrderException("La línea es obligatoria");
		}
		return items.stream()
				.filter(item -> item.getPublicId().equals(itemPublicId))
				.findFirst()
				.orElseThrow(() -> new OrderException("La línea no pertenece a este pedido"));
	}

	
	
	private void recalcularTotales(LocalDateTime now) {
		BigDecimal importeConIva = items.stream().map(OrderItem::getLineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal divisor = BigDecimal.ONE.add(taxRate.divide(CIEN));
		this.subtotal = importeConIva.divide(divisor, 2, RoundingMode.HALF_UP);
		this.taxAmount = importeConIva.subtract(subtotal);
		this.total = subtotal.add(taxAmount);
		this.updatedAt = now;
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
	public Integer getOrderNumber() {
		return orderNumber;
	}
	public OrderType getOrderType() {
		return orderType;
	}
	public OrderStatus getStatus() {
		return status;
	}
	public Long getTableId() {
		return tableId;
	}
	public Integer getNumPersonas() {
		return numPersonas;
	}
	public BigDecimal getSubtotal() {
		return subtotal;
	}
	public BigDecimal getTaxRate() {
		return taxRate;
	}
	public BigDecimal getTaxAmount() {
		return taxAmount;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public BigDecimal getPaidAmount() {
		return paidAmount;
	}
	public LocalDateTime getPaidAt() {
		return paidAt;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	public LocalDateTime getCookedAt() {
		return cookedAt;
	}
	public String getIdempotencyKey() {
		return idempotencyKey;
	}
	public LocalDateTime getClientTimestamp() {
		return clientTimestamp;
	}
	public SyncStatus getSyncStatus() {
		return syncStatus;
	}
	@Override
	public String toString() {
		return "Order [id=" + id + ", tenantId=" + tenantId + ", publicId=" + publicId + ", orderNumber="
				+ orderNumber + ", orderType=" + orderType + ", status=" + status + ", tableId=" + tableId
				+ ", items=" + items.size() + ", subtotal=" + subtotal + ", total=" + total + "]";
	}

}
