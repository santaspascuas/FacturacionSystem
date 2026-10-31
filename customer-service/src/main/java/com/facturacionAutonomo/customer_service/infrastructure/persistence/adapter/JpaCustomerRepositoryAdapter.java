package com.facturacionAutonomo.customer_service.infrastructure.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.customer_service.application.port.out.CustomerRepositoryPort;
import com.facturacionAutonomo.customer_service.domain.model.Customer;
import com.facturacionAutonomo.customer_service.infrastructure.persistence.entity.CustomerEntity;
import com.facturacionAutonomo.customer_service.infrastructure.persistence.mapper.CustomerEntityMapper;
import com.facturacionAutonomo.customer_service.infrastructure.persistence.repository.SpringDataCustomerRepository;


@Repository
public class JpaCustomerRepositoryAdapter implements CustomerRepositoryPort {

	private static final Logger log = LoggerFactory.getLogger(JpaCustomerRepositoryAdapter.class);

	private final SpringDataCustomerRepository springdataCustomerRepo;

	public JpaCustomerRepositoryAdapter(SpringDataCustomerRepository springdataCustomerRepo) {
		super();
		this.springdataCustomerRepo = springdataCustomerRepo;
	}

	@Override
	@Transactional
	public Customer save(Customer customer) {
		//Separar en actualizar o Guardar.
		
		if (customer == null || customer.getTenantId() == null) {
			throw new IllegalArgumentException("El customer y su tenantId son obligatorios para guardar");
		}
		CustomerEntity existente = springdataCustomerRepo.findByTenantId(customer.getTenantId()).orElse(null);
		CustomerEntity guardada;

		if (existente == null) {
			CustomerEntity nueva = CustomerEntityMapper.dominioToJpa(customer);
			log.debug("[save] creando customer tenantId: {}", customer.getTenantId());
			guardada = springdataCustomerRepo.save(nueva);
		} else {
			CustomerEntityMapper.actualizarJpa(customer, existente);
			log.debug("[save] actualizando customer tenantId: {} id: {}", customer.getTenantId(), existente.getId());
			guardada = springdataCustomerRepo.save(existente);
		}

		return CustomerEntityMapper.jpaToDominio(guardada);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Customer> findByTenantId(UUID tenantId) {
		return springdataCustomerRepo.findByTenantId(tenantId).map(CustomerEntityMapper::jpaToDominio);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByTenantId(UUID tenantId) {
		return springdataCustomerRepo.existsByTenantId(tenantId);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean existsByEmail(String email) {
		// Sin distinguir mayúsculas, igual que el índice único uk_customers_email_lower.
		return springdataCustomerRepo.existsByEmailIgnoreCase(email);
	}

	// Se carga la entidad y se borra con delete(): así se aplica la cascada REMOVE a las direcciones.
	// Un DELETE en JPQL se saltaría la cascada y chocaría con la FK de addresses.
	@Override
	@Transactional
	public void deleteByTenantId(UUID tenantId) {
		springdataCustomerRepo.findByTenantId(tenantId).ifPresent(entidad -> {
			log.debug("[deleteByTenantId] borrando customer tenantId: {} id: {} direcciones: {}", tenantId,
					entidad.getId(), entidad.getAddresses().size());
			springdataCustomerRepo.delete(entidad);
		});
	}
}
