package com.facturacionAutonomo.product_service.application.service.payments;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.product_service.application.command.payments.RegistrarPaymentCommand;
import com.facturacionAutonomo.product_service.application.port.OUT.OrderRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.OUT.PaymentRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.in.payment.RegistrarPaymentUseCase;
import com.facturacionAutonomo.product_service.application.result.PaymentResult;
import com.facturacionAutonomo.product_service.application.result.RegistrarPaymentResult;
import com.facturacionAutonomo.product_service.application.service.finder.OrderFinder;
import com.facturacionAutonomo.product_service.domain.exception.PaymentException;
import com.facturacionAutonomo.product_service.domain.model.Order;
import com.facturacionAutonomo.product_service.domain.model.Payment;

@Service
public class RegistrarPaymentService implements RegistrarPaymentUseCase{

	private static final Logger log = LoggerFactory.getLogger(RegistrarPaymentService.class);

	private final PaymentRepositoryPort paymentrepository;
	private final OrderRepositoryPort orderrepository;
	private final OrderFinder finder;
	private final Clock clock;


	public RegistrarPaymentService(PaymentRepositoryPort paymentrepository, OrderRepositoryPort orderrepository,
			OrderFinder finder, Clock clock) {
		super();
		this.paymentrepository = paymentrepository;
		this.orderrepository = orderrepository;
		this.finder = finder;
		this.clock = clock;
	}


	// Una sola transacción para el cobro y el pedido: o se guardan los dos o ninguno.
	// Si dos cobros del mismo pedido llegan a la vez, el @Version del pedido hace que el segundo falle (409)
	// en vez de sumar los dos sobre el mismo "pendiente" y cobrar de más.
	@Override
	@Transactional
	public RegistrarPaymentResult execute(RegistrarPaymentCommand command) {

		finder.tenantObligatorio(command.tenantId());
		log.info("[RegistrarPaymentService] cobro de pedido tenantId: {}", command.tenantId());

		// 1. El pedido del tenant (404 si no existe o es de otro restaurante).
		Order order = finder.pedidoDelTenant(command.tenantId(), command.orderPublicId());

		// 2. Idempotencia: si el TPV reintenta un cobro que ya llegó, se devuelve el existente sin cobrar otra vez.
		String clave = Order.claveOpcional(command.idempotencyKey());
		Optional<Payment> existente = paymentrepository.findByTenantIdAndIdempotencyKey(command.tenantId(), clave);
		if(existente.isPresent()) {
			Payment cobroAnterior = existente.get();
			// La clave identifica UN cobro: si llega con otro pedido, es un error del TPV, no un reintento.
			if(!cobroAnterior.getOrderId().equals(order.getId())) {
				throw new PaymentException("La clave de idempotencia ya se usó en un cobro de otro pedido");
			}
			log.info("[RegistrarPaymentService] cobro ya registrado con la misma clave de idempotencia");
			return new RegistrarPaymentResult(PaymentResult.convertirA(cobroAnterior), finder.aResult(order));
		}

		// 3. Crear el cobro: el dominio calcula lo aplicado, el efectivo y el cambio a partir de lo pendiente
		//    (efectivo: min(entregado, pendiente); tarjeta: entera y sin pasarse).
		LocalDateTime ahora = LocalDateTime.now(clock);
		Payment pago = Payment.crearUnPayment(command.tenantId(), order.getId(), order.getImportePendiente(),
				command.paymentType(), command.cantidad(), clave, ahora);

		// 4. Sumarlo al pedido. El pedido lo vuelve a validar (abierto, con líneas, sin pasarse del total)
		//    y, si queda todo cobrado, pasa a CIERRAYPAGA.
		order.registrarCobro(pago.getAmount(), ahora);

		// 5. Guardar los dos. Primero el cobro (su pedido ya existe) y después el pedido, que comprueba el @Version.
		Payment guardado = paymentrepository.save(pago);
		Order actualizado = orderrepository.update(order);

		return new RegistrarPaymentResult(PaymentResult.convertirA(guardado), finder.aResult(actualizado));
	}

}
