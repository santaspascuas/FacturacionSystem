package com.facturacionAutonomo.auth_service.application.port.out;

public interface TokenHasherPort {
	String hash(String rawToken);
}
