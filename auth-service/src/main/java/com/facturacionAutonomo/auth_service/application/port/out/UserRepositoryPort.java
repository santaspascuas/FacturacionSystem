package com.facturacionAutonomo.auth_service.application.port.out;

import java.util.Optional;
import java.util.UUID;

import com.facturacionAutonomo.auth_service.domain.model.Email;
import com.facturacionAutonomo.auth_service.domain.model.User;

public interface UserRepositoryPort {
	User save(User user);

	Optional<User> findByEmail(Email email);

	boolean existsByEmail(Email email);

	Optional<User> findById(UUID id);
}
