package com.facturacionAutonomo.product_service.application.service.orders;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.product_service.application.command.orders.RegistrarOrderCommand;
import com.facturacionAutonomo.product_service.application.port.OUT.OrderRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.OUT.TipoImpositivoPort;
import com.facturacionAutonomo.product_service.application.port.in.order.RegistrarOrderUseCase;
import com.facturacionAutonomo.product_service.application.result.OrderResult;
import com.facturacionAutonomo.product_service.application.service.finder.OrderFinder;
import com.facturacionAutonomo.product_service.application.service.finder.TablesItemFinder;
import com.facturacionAutonomo.product_service.domain.exception.MesaConPedidoAbiertoException;
import com.facturacionAutonomo.product_service.domain.model.Order;
import com.facturacionAutonomo.product_service.domain.model.OrderItem;
import com.facturacionAutonomo.product_service.domain.model.TableRestautant;

@Service
public class RegistrarOrderService implements RegistrarOrderUseCase{

	private static final Logger log = LoggerFactory.getLogger(RegistrarOrderService.class);

	private final OrderRepositoryPort orderrepository;
	private final OrderFinder finder;
	private final TablesItemFinder tablesfinder;
	private final TipoImpositivoPort tipoimpositivo;
	private final Clock clock;


	public RegistrarOrderService(OrderRepositoryPort orderrepository, OrderFinder finder,
			TablesItemFinder tablesfinder, TipoImpositivoPort tipoimpositivo, Clock clock) {
		super();
		this.orderrepository = orderrepository;
		this.finder = finder;
		this.tablesfinder = tablesfinder;
		this.tipoimpositivo = tipoimpositivo;
		this.clock = clock;
	}


	@Override
	@Transactional
	public OrderResult execute(RegistrarOrderCommand command) {
		finder.tenantObligatorio(command.tenantId());
		log.info("[RegistrarOrderService] alta de pedido tenantId: {}", command.tenantId());
		log.debug("idempotencia:{}", command.idempotencyKey());
		// La clave es opcional: sin ella (null o en blanco) no se comprueba nada. Antes .trim() sobre null daba un 500.
		String hashpotencia = Order.claveOpcional(command.idempotencyKey());
		if(hashpotencia!= null) {
			log.info("Envias un hash del tpv para ver si esta uya registrado");
			Optional<Order> existente = orderrepository.findByTenantIdAndIdempotencyKey(command.tenantId(), hashpotencia);
			log.debug("lo que obtenemos de la database:{}",existente.toString() );
			if(existente.isPresent()) {
				log.info("[RegistrarOrderService] pedido ya registrado con la misma clave de idempotencia");
				return finder.aResult(existente.get());
			}
		}
		
		//Al no estar registrado-> insercion nueva. Buscamos su mesa
		TableRestautant mesa = null;
		if(command.tablePublicId()!= null) {
			log.info("Se manda cuando es comer aqui");
			 mesa = tablesfinder.encuentraTenatandPublic(command.tenantId(), command.tablePublicId());
			log.debug("Se ha encontrado la mesa:{}", mesa.toString());
			//Hay que ver si la mesa tiene pedidos abiertos
			if(orderrepository.tienePedidoAbierto(mesa.getId())) {
				log.info("Tiene abierto un pedido ya");
				throw new MesaConPedidoAbiertoException("La mesa " + mesa.getTableNumber() + " ya tiene un pedido abierto");
			}
		}
		//Buscamos los productos (una sola consulta) y preparamos las líneas antes de crear nada: si falta uno, 404.
		LocalDateTime ahora = LocalDateTime.now(clock);
		// Las líneas del alta llevan la misma clave que el pedido: son su primer envío a cocina.
		List<OrderItem> lineas = finder.crearLineas(command.tenantId(), command.lineas(), hashpotencia, ahora);

		//Creamos el pedido.
		int numero = orderrepository.findMaxOrderNumberByTenantId(command.tenantId()) + 1;
		log.debug("El numero obtenido para el nuevo order:{}", numero);
		// El tipo de IVA vigente ahora queda guardado en el pedido; los importes los calcula Order al añadir cada línea.
		// Se usa el mismo "ahora" que la fecha de creación: tipo y fecha del pedido siempre cuadran.
		Order order = Order.crearNuevaOrder(command.tenantId(), numero, command.orderType(),
		mesa != null ? mesa.getId() : null, command.numPersonas(), tipoimpositivo.tipoVigenteEn(ahora),
		hashpotencia, command.clientTimestamp(), ahora);
		log.debug("creamos la order:{}", order.toString());

		lineas.forEach(linea -> order.addItem(linea, ahora));

		return OrderResult.convertirA(orderrepository.save(order), mesa);
	}

}
