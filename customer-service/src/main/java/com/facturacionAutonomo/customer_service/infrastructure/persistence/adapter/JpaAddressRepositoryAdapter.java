package com.facturacionAutonomo.customer_service.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.facturacionAutonomo.customer_service.application.port.out.AddressRepositoryPort;
import com.facturacionAutonomo.customer_service.domain.exception.CustomerNoEncontradoException;
import com.facturacionAutonomo.customer_service.domain.model.Address;
import com.facturacionAutonomo.customer_service.infrastructure.persistence.entity.AddressEntity;
import com.facturacionAutonomo.customer_service.infrastructure.persistence.entity.CustomerEntity;
import com.facturacionAutonomo.customer_service.infrastructure.persistence.mapper.AddressEntityMapper;
import com.facturacionAutonomo.customer_service.infrastructure.persistence.repository.SpringDataAddressRepository;
import com.facturacionAutonomo.customer_service.infrastructure.persistence.repository.SpringDataCustomerRepository;

@Repository
public class JpaAddressRepositoryAdapter implements AddressRepositoryPort{

	private static final Logger log = LoggerFactory.getLogger(JpaAddressRepositoryAdapter.class);

	private final SpringDataAddressRepository springdataaddresrepo;
	private final SpringDataCustomerRepository springdataCustomerRepo;

	public JpaAddressRepositoryAdapter(SpringDataAddressRepository springdataaddresrepo,
			SpringDataCustomerRepository springdataCustomerRepo) {
		super();
		this.springdataaddresrepo = springdataaddresrepo;
		this.springdataCustomerRepo = springdataCustomerRepo;
	}

	@Override
	@Transactional
	public Address save(UUID tenantId, Address address) {

		if (tenantId == null || address == null) {
			throw new IllegalArgumentException("El tenant y la direccion son obligatorios para guardar");
		}
		AddressEntity encontrado = springdataaddresrepo
				.findByPublicIdAndCustomerTenantId(address.getPublicId(), tenantId)
				.orElse(null);
		
		AddressEntity guardado;
		
		if (encontrado != null) {
			// Existe y es del tenant -> actualización sobre la misma fila (mismo address_id y customer).
			log.debug("Entramos en que ha encontrado datos :{}", encontrado.getPublicId());
			AddressEntityMapper.actualizarJpa(address, encontrado);
			 guardado = springdataaddresrepo.save(encontrado);
		}else {
			AddressEntity nueva = AddressEntityMapper.dominioToJpa(address);
			CustomerEntity nuevo = springdataCustomerRepo.findByTenantId(tenantId).orElseThrow(
					()->new CustomerNoEncontradoException("El tenant no tiene un customer registrado"));
			nueva.setCustomer(nuevo);
			log.debug("[save] creando direccion publicId: {} para customer id: {}", nueva.getPublicId(),
					nuevo.getId());
			guardado = springdataaddresrepo.save(nueva);
		}


		return AddressEntityMapper.jpaToDominio(guardado);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Address> findByPublicIdAndTenantId(UUID publicId, UUID tenantId) {
		return springdataaddresrepo.findByPublicIdAndCustomerTenantId(publicId, tenantId)
				.map(AddressEntityMapper::jpaToDominio);
	}

	// El delete derivado carga la entidad y la borra con remove(): necesita transacción.
	@Override
	@Transactional
	public boolean deleteByPublicIdAndTenantId(UUID publicId, UUID tenantId) {
		long borradas = springdataaddresrepo.deleteByPublicIdAndCustomerTenantId(publicId, tenantId);
		log.debug("[delete] direccion publicId: {} filas borradas: {}", publicId, borradas);
		return borradas > 0;
	}

	@Override
	@Transactional
	public void desactivarFiscal(UUID tenantId) {
		int desactivadas = springdataaddresrepo.desactivarFiscal(tenantId);
		log.debug("[desactivarFiscal] tenantId: {} filas desactivadas: {}", tenantId, desactivadas);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Address> findByTenantId(UUID tenantId) {
		return springdataaddresrepo.findByCustomerTenantIdOrderByAddressIdAsc(tenantId)
				.stream()
				.map(AddressEntityMapper::jpaToDominio)
				.toList();
	}

}
