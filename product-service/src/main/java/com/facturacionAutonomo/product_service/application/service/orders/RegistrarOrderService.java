package com.facturacionAutonomo.product_service.application.service.orders;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.product_service.application.command.orders.LineaOrderCommand;
import com.facturacionAutonomo.product_service.application.command.orders.RegistrarOrderCommand;
import com.facturacionAutonomo.product_service.application.port.OUT.MenuItemRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.OUT.OrderRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.OUT.TipoImpositivoPort;
import com.facturacionAutonomo.product_service.application.port.in.order.RegistrarOrderUseCase;
import com.facturacionAutonomo.product_service.application.result.OrderResult;
import com.facturacionAutonomo.product_service.application.service.finder.OrderFinder;
import com.facturacionAutonomo.product_service.application.service.finder.TablesItemFinder;
import com.facturacionAutonomo.product_service.domain.exception.MenuItemNotFoundException;
import com.facturacionAutonomo.product_service.domain.exception.MesaConPedidoAbiertoException;
import com.facturacionAutonomo.product_service.domain.exception.OrderException;
import com.facturacionAutonomo.product_service.domain.model.MenuItem;
import com.facturacionAutonomo.product_service.domain.model.Order;
import com.facturacionAutonomo.product_service.domain.model.OrderItem;
import com.facturacionAutonomo.product_service.domain.model.TableRestautant;

@Service
public class RegistrarOrderService implements RegistrarOrderUseCase{

	private static final Logger log = LoggerFactory.getLogger(RegistrarOrderService.class);

	private final OrderRepositoryPort orderrepository;
	private final MenuItemRepositoryPort menuitemrepository;
	private final OrderFinder finder;
	private final TablesItemFinder tablesfinder;
	private final TipoImpositivoPort tipoimpositivo;
	private final Clock clock;


	public RegistrarOrderService(OrderRepositoryPort orderrepository, MenuItemRepositoryPort menuitemrepository,
			OrderFinder finder, TablesItemFinder tablesfinder, TipoImpositivoPort tipoimpositivo, Clock clock) {
		super();
		this.orderrepository = orderrepository;
		this.menuitemrepository = menuitemrepository;
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
		String hashpotencia = command.idempotencyKey().trim();
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
		//Buscamos los productos y depuramos.
		Map<UUID, MenuItem> productos = buscarProductos(command.tenantId(), command.lineas());
		
		//Creamos el pedido.
		LocalDateTime ahora = LocalDateTime.now(clock);
		int numero = orderrepository.findMaxOrderNumberByTenantId(command.tenantId()) + 1;
		log.debug("El numero obtenido para el nuevo order:{}", numero);
		// El tipo de IVA vigente ahora queda guardado en el pedido; los importes los calcula Order al añadir cada línea.
		// Se usa el mismo "ahora" que la fecha de creación: tipo y fecha del pedido siempre cuadran.
		Order order = Order.crearNuevaOrder(command.tenantId(), numero, command.orderType(),
		mesa != null ? mesa.getId() : null, command.numPersonas(), tipoimpositivo.tipoVigenteEn(ahora),
		hashpotencia, command.clientTimestamp(), ahora);
		log.debug("creamos la order:{}", order.toString());

		for(LineaOrderCommand linea : command.lineas()) {
			MenuItem producto = productos.get(linea.menuItemPublicId());
			order.addItem(OrderItem.crearOrderItem(producto.getId(), producto.getName(), producto.getBasePrice(),
					linea.quantity(), linea.notes(), ahora), ahora);
		}
			
		return OrderResult.convertirA(orderrepository.save(order), mesa);
	}
	
	
	
	
	
	
	
	
	
	private Map<UUID, MenuItem> buscarProductos(UUID tenantId, List<LineaOrderCommand> lineas) {
		log.info("Entramos en el buscarProductos");
		Set<UUID> pedidos = new HashSet<>();
		lineas.forEach(linea->
		pedidos.add(linea.menuItemPublicId()));
		log.debug("IDs de productos únicos recopilados en el Set: {}", pedidos);
		
		//Buscamos con muchos ids
		List<MenuItem> listaEncontrados = menuitemrepository.findActiveByTenantIdAndPublicIds(tenantId, pedidos);
		listaEncontrados.forEach(lista->log.debug("lista:{}",lista.toString()));
		Map<UUID, MenuItem> encontrados = new HashMap<>();
		for (MenuItem item : listaEncontrados) {
	        encontrados.put(item.getPublicId(), item);
	    }
		
		encontrados.forEach((id,itema)->{
			log.debug("Plato ID: {} - Precio: {}", id, itema.toString());
		});
		
		if(encontrados.size() != pedidos.size()) {
			log.warn("Se comparan para saber si el tamaño de las colecciones son lo mismo para depurar");
			throw new MenuItemNotFoundException("Algún producto del pedido no existe o no está disponible");
		}
		
		
		
		return encontrados;
	}


}
