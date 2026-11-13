package com.facturacionAutonomo.product_service.application.port.OUT;

public interface PasswordHasherPort {
	
	String hash(String rawPassword);
	boolean matches(String rawPassword, String passwordHash);

}
