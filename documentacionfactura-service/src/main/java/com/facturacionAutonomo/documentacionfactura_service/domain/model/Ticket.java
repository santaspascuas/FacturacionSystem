package com.facturacionAutonomo.documentacionfactura_service.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import com.facturacionAutonomo.documentacionfactura_service.domain.exception.DatoInvalidoException;
import com.facturacionAutonomo.documentacionfactura_service.domain.exception.EmisorSinDatosFiscalesException;
import com.facturacionAutonomo.documentacionfactura_service.domain.exception.PedidoNoPagadoException;
import com.facturacionAutonomo.documentacionfactura_service.domain.exception.TicketIncoherenteException;

/**
 * Ticket (factura simplificada) de un pedido pagado. Guarda una COPIA de los datos del pedido y del emisor en el
 * momento de emitirlo: si después cambian en sus servicios, el ticket emitido no cambia.
 *
 * Los datos fiscales son inmutables. Lo único que evoluciona es el control de impresión (estado, veces, error):
 * cada cambio devuelve un Ticket nuevo, como el resto de modelos del monorepo.
 */
public class Ticket {

	private static final int MAX_LONGITUD_ERROR = 500;

	private final UUID publicId;
	private final UUID tenantId;
	private final TicketNumber numero;
	private final LocalDateTime emitidoEn;

	// Referencia al pedido de product-service
	private final UUID orderPublicId;
	private final Integer orderNumber;
	private final OrderType orderType;
	private final Integer tableNumber;
	private final Integer numPersonas;
	private final LocalDateTime pagadoEn;

	private final Issuer emisor;

	private final BigDecimal baseImponible;
	private final BigDecimal tipoIva;
	private final BigDecimal cuotaIva;
	private final BigDecimal total;

	private final List<TicketLine> lineas;
	private final List<TicketPayment> cobros;

	// Control de impresión: lo único que cambia después de emitir
	private final PrintStatus estadoImpresion;
	private final int vecesImpreso;
	private final String ultimoError;

	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	private Ticket(UUID publicId, UUID tenantId, TicketNumber numero, LocalDateTime emitidoEn, UUID orderPublicId,
			Integer orderNumber, OrderType orderType, Integer tableNumber, Integer numPersonas,
			LocalDateTime pagadoEn, Issuer emisor, BigDecimal baseImponible, BigDecimal tipoIva,
			BigDecimal cuotaIva, BigDecimal total, List<TicketLine> lineas, List<TicketPayment> cobros,
			PrintStatus estadoImpresion, int vecesImpreso, String ultimoError, LocalDateTime createdAt,
			LocalDateTime updatedAt) {
		this.publicId = publicId;
		this.tenantId = tenantId;
		this.numero = numero;
		this.emitidoEn = emitidoEn;
		this.orderPublicId = orderPublicId;
		this.orderNumber = orderNumber;
		this.orderType = orderType;
		this.tableNumber = tableNumber;
		this.numPersonas = numPersonas;
		this.pagadoEn = pagadoEn;
		this.emisor = emisor;
		this.baseImponible = baseImponible;
		this.tipoIva = tipoIva;
		this.cuotaIva = cuotaIva;
		this.total = total;
		this.lineas = lineas == null ? List.of() : List.copyOf(lineas);
		this.cobros = cobros == null ? List.of() : List.copyOf(cobros);
		this.estadoImpresion = estadoImpresion;
		this.vecesImpreso = vecesImpreso;
		this.ultimoError = ultimoError;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	/**
	 * Comprueba que con este pedido y este emisor se puede emitir un ticket. El servicio la llama ANTES de pedir el
	 * número fiscal (para no gastarlo en un ticket que no se va a emitir) y emitir() la vuelve a aplicar por
	 * seguridad: la regla vive solo aquí.
	 */
	public static void validarEmision(Order pedido, Issuer emisor) {
		if (pedido == null) {
			throw new DatoInvalidoException("El pedido es obligatorio");
		}
		if (!pedido.estaPagado()) {
			throw new PedidoNoPagadoException("El pedido " + pedido.getOrderNumber() + " no está pagado del todo");
		}
		if (emisor == null || !emisor.tieneDatosFiscales()) {
			throw new EmisorSinDatosFiscalesException("El restaurante no tiene razón social o NIF registrados");
		}
		if (pedido.getLineas().isEmpty()) {
			throw new TicketIncoherenteException("El pedido " + pedido.getOrderNumber() + " no tiene líneas");
		}
		if (pedido.getBaseImponible() == null || pedido.getCuotaIva() == null || pedido.getTipoIva() == null) {
			throw new TicketIncoherenteException(
					"El pedido " + pedido.getOrderNumber() + " no trae base, tipo o cuota de IVA");
		}
		exigirIgual(pedido.getBaseImponible().add(pedido.getCuotaIva()), pedido.getTotal(), pedido, "base + cuota");
		exigirIgual(sumar(pedido.getLineas(), TicketLine::getImporte), pedido.getTotal(), pedido,
				"la suma de las líneas");
		exigirIgual(sumar(pedido.getCobros(), TicketPayment::getImporte), pedido.getTotal(), pedido,
				"la suma de los cobros");
	}

	/** Emite el ticket de un pedido pagado. Queda PENDIENTE de imprimir. */
	public static Ticket emitir(UUID tenantId, Order pedido, Issuer emisor, TicketNumber numero,
			LocalDateTime ahora) {
		if (tenantId == null) {
			throw new DatoInvalidoException("El tenant es obligatorio");
		}
		if (numero == null) {
			throw new DatoInvalidoException("El número del ticket es obligatorio");
		}
		validarEmision(pedido, emisor);

		return new Ticket(UUID.randomUUID(), tenantId, numero, ahora, pedido.getPublicId(), pedido.getOrderNumber(),
				pedido.getOrderType(), pedido.getTableNumber(), pedido.getNumPersonas(), pedido.getPagadoEn(), emisor,
				pedido.getBaseImponible(), pedido.getTipoIva(), pedido.getCuotaIva(), pedido.getTotal(),
				pedido.getLineas(), pedido.getCobros(), PrintStatus.PENDIENTE, 0, null, ahora, ahora);
	}

	public static Ticket restoreData(UUID publicId, UUID tenantId, TicketNumber numero, LocalDateTime emitidoEn,
			UUID orderPublicId, Integer orderNumber, OrderType orderType, Integer tableNumber, Integer numPersonas,
			LocalDateTime pagadoEn, Issuer emisor, BigDecimal baseImponible, BigDecimal tipoIva,
			BigDecimal cuotaIva, BigDecimal total, List<TicketLine> lineas, List<TicketPayment> cobros,
			PrintStatus estadoImpresion, int vecesImpreso, String ultimoError, LocalDateTime createdAt,
			LocalDateTime updatedAt) {
		return new Ticket(publicId, tenantId, numero, emitidoEn, orderPublicId, orderNumber, orderType, tableNumber,
				numPersonas, pagadoEn, emisor, baseImponible, tipoIva, cuotaIva, total, lineas, cobros,
				estadoImpresion, vecesImpreso, ultimoError, createdAt, updatedAt);
	}

	/** La impresora aceptó el ticket. */
	public Ticket registrarImpresion(LocalDateTime ahora) {
		return conImpresion(PrintStatus.IMPRESO, vecesImpreso + 1, null, ahora);
	}

	/** La impresora falló. vecesImpreso no sube: ese intento no llegó al papel. */
	public Ticket registrarFalloImpresion(String error, LocalDateTime ahora) {
		return conImpresion(PrintStatus.ERROR, vecesImpreso, recortar(error), ahora);
	}

	/** Si ya salió una vez en papel, la siguiente impresión lleva la marca "COPIA". */
	public boolean siguienteEsCopia() {
		return vecesImpreso > 0;
	}

	private Ticket conImpresion(PrintStatus estado, int veces, String error, LocalDateTime ahora) {
		return new Ticket(publicId, tenantId, numero, emitidoEn, orderPublicId, orderNumber, orderType, tableNumber,
				numPersonas, pagadoEn, emisor, baseImponible, tipoIva, cuotaIva, total, lineas, cobros, estado,
				veces, error, createdAt, ahora);
	}

	// compareTo y no equals: para BigDecimal, 22.0 y 22.00 son iguales en valor pero no en equals().
	private static void exigirIgual(BigDecimal calculado, BigDecimal total, Order pedido, String que) {
		if (calculado == null || total == null || calculado.compareTo(total) != 0) {
			throw new TicketIncoherenteException("Pedido " + pedido.getOrderNumber() + ": " + que + " (" + calculado
					+ ") no coincide con el total (" + total + ")");
		}
	}

	private static <T> BigDecimal sumar(List<T> elementos, Function<T, BigDecimal> importe) {
		return elementos.stream().map(importe).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	// El mensaje de una excepción puede ser muy largo; la columna tiene un límite.
	private static String recortar(String error) {
		if (error == null || error.length() <= MAX_LONGITUD_ERROR) {
			return error;
		}
		return error.substring(0, MAX_LONGITUD_ERROR);
	}

	public UUID getPublicId() {
		return publicId;
	}

	public UUID getTenantId() {
		return tenantId;
	}

	public TicketNumber getNumero() {
		return numero;
	}

	public LocalDateTime getEmitidoEn() {
		return emitidoEn;
	}

	public UUID getOrderPublicId() {
		return orderPublicId;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public OrderType getOrderType() {
		return orderType;
	}

	public Integer getTableNumber() {
		return tableNumber;
	}

	public Integer getNumPersonas() {
		return numPersonas;
	}

	public LocalDateTime getPagadoEn() {
		return pagadoEn;
	}

	public Issuer getEmisor() {
		return emisor;
	}

	public BigDecimal getBaseImponible() {
		return baseImponible;
	}

	public BigDecimal getTipoIva() {
		return tipoIva;
	}

	public BigDecimal getCuotaIva() {
		return cuotaIva;
	}

	public BigDecimal getTotal() {
		return total;
	}

	/** Lista inmutable. */
	public List<TicketLine> getLineas() {
		return lineas;
	}

	/** Lista inmutable. */
	public List<TicketPayment> getCobros() {
		return cobros;
	}

	public PrintStatus getEstadoImpresion() {
		return estadoImpresion;
	}

	public int getVecesImpreso() {
		return vecesImpreso;
	}

	public String getUltimoError() {
		return ultimoError;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	@Override
	public String toString() {
		return "Ticket [numero=" + (numero == null ? null : numero.formateado()) + ", tenantId=" + tenantId
				+ ", orderNumber=" + orderNumber + ", total=" + total + ", estadoImpresion=" + estadoImpresion
				+ ", vecesImpreso=" + vecesImpreso + "]";
	}
}
