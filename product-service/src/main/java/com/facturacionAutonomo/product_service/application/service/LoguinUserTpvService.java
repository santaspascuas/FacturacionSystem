package com.facturacionAutonomo.product_service.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.facturacionAutonomo.product_service.application.command.LoguinUserTpvCommand;
import com.facturacionAutonomo.product_service.application.port.OUT.PasswordHasherPort;
import com.facturacionAutonomo.product_service.application.port.OUT.UserTpvRepositoryPort;
import com.facturacionAutonomo.product_service.application.port.in.LoguinUserTpvUseCase;
import com.facturacionAutonomo.product_service.application.result.UserTpvLoguinResult;
import com.facturacionAutonomo.product_service.domain.exception.UserTpvException;
import com.facturacionAutonomo.product_service.domain.model.UsersTpv;

@Service
public class LoguinUserTpvService implements LoguinUserTpvUseCase {
	
	
	private static final Logger log = LoggerFactory.getLogger(LoguinUserTpvService.class);
	private final PasswordHasherPort passwordHashear;
	private final UserTpvRepositoryPort usertpvrepositoryport;
	
	
	
	

	public LoguinUserTpvService(PasswordHasherPort passwordHashear, UserTpvRepositoryPort usertpvrepositoryport) {
		super();
		this.passwordHashear = passwordHashear;
		this.usertpvrepositoryport = usertpvrepositoryport;
	}





	@Override
	public UserTpvLoguinResult execute(LoguinUserTpvCommand command) {
		
		log.info("[Loguin:[UserTpvLoguinResult execute] loguin del trabajador", command.tenantId());
		
		if(command.tenantId() == null) {
			throw new UserTpvException("El tenant es obligatorio");
		}
		
		
		
		//La idea es encontrar el user y comprobar la contrasena
		
		UsersTpv usuariotpv  = usertpvrepositoryport.findByByTenantIdAndUsername(command.tenantId(), command.username())
				.orElseThrow(
						()->new UserTpvException("El usuario no ha sido encontrado"));
		
		// matches recibe primero la contraseña en claro y después el hash guardado.
		if(!passwordHashear.matches(command.password_hash(), usuariotpv.getPassword_hash())) {
			throw new UserTpvException("Error en la identificacion del usuario");
		}
		
		//Pasa el filtro->trasnformamos para lo que queremos.
		
		return UserTpvLoguinResult.convertirA(usuariotpv);
		
	}

}
