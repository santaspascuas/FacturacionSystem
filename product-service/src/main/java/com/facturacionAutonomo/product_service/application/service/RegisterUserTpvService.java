package com.facturacionAutonomo.product_service.application.service;

import java.time.Clock;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.facturacionAutonomo.product_service.application.command.RegistrarUserTpvCommand;
import com.facturacionAutonomo.product_service.application.port.OUT.PasswordHasherPort;
import com.facturacionAutonomo.product_service.application.port.OUT.UserTpvRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.in.RegisterUserTpvUseCase;
import com.facturacionAutonomo.product_service.application.result.UserTpvResult;
import com.facturacionAutonomo.product_service.domain.exception.UserTpvException;
import com.facturacionAutonomo.product_service.domain.model.Role;
import com.facturacionAutonomo.product_service.domain.model.UsersTpv;

@Service
public class RegisterUserTpvService implements RegisterUserTpvUseCase{
	
	private static final Logger log = LoggerFactory.getLogger(RegisterUserTpvService.class);
	private final Clock clock;
	private final PasswordHasherPort passwordHashear;
	private final UserTpvRepositoryPort usertpvrepositoryport;

	public RegisterUserTpvService(Clock clock, PasswordHasherPort passwordHashear,
			UserTpvRepositoryPort usertpvrepositoryport) {
		super();
		this.clock = clock;
		this.passwordHashear = passwordHashear;
		this.usertpvrepositoryport = usertpvrepositoryport;
	}

	@Override
	public UserTpvResult execute(RegistrarUserTpvCommand command) {
		log.info("[RegistrarUsertpv] alta con el tenantId :{}", command.tenantId());
		//cubrimos que sea los datos enviados correctamente
		
		if(command.tenantId() == null) {
			throw new UserTpvException("El tenant es obligatorio");
		}
		//username como diferencia en cada tenant
		if(usertpvrepositoryport.existsByTenantIdAndUsername(command.tenantId(), command.username())) {
			throw new UserTpvException("El usuario " + command.username() + " ya está registrado");
		}	
		//La contrasena debe de ser hasheada.
		String hasheado = passwordHashear.hash(command.password_hash());
		log.debug("La contrasena hasheada:{}", hasheado);
		
		//Generamos las horas
		
		LocalDateTime now = LocalDateTime.now(clock);
		//LLAMAMOS AL SERVICIO-> creamos usertpv
		UsersTpv usuariotpv = UsersTpv.crearUsertpv(command.tenantId(),command.username(), hasheado, Role.Trabajador, now);
		//creado->guardamos
		
		
		UsersTpv guardado = usertpvrepositoryport.save(command.tenantId(), usuariotpv);
		
		//mapeamos a resultado
		
		return UserTpvResult.convertirA(guardado);
	}

}
