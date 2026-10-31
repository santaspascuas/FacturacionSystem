package com.facturacionAutonomo.customer_service.infrastructure.persistence.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "addresses")
public class AddressEntity {

	@Id
	@Column(name = "address_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_id_generator")
	@SequenceGenerator(name = "address_id_generator", sequenceName = "address_id_seq", allocationSize = 1)
	private Long addressId;

	@Column(name = "public_id", nullable = false, unique = true, updatable = false)
	private UUID publicId;

	@Column(nullable = false, length = 150)
	private String street;

	@Column(name = "postal_code", nullable = false, length = 10)
	private String postalCode;

	@Column(nullable = false, length = 100)
	private String city;

	@Column(length = 100)
	private String province;

	@Column(nullable = false, length = 2)
	private String country;

	@Column(nullable = false)
	private boolean active;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "customer_id", nullable = false)
	private CustomerEntity customer;

	public AddressEntity() {
		super();
	}

	public Long getAddressId() {
		return addressId;
	}

	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}

	public UUID getPublicId() {
		return publicId;
	}

	public void setPublicId(UUID publicId) {
		this.publicId = publicId;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public CustomerEntity getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerEntity customer) {
		this.customer = customer;
	}

	// Sin el customer: evita recursión y cargas lazy fuera de transacción.
	@Override
	public String toString() {
		return "AddressEntity [addressId=" + addressId + ", publicId=" + publicId + ", street=" + street
				+ ", postalCode=" + postalCode + ", city=" + city + ", province=" + province + ", country="
				+ country + ", active=" + active + "]";
	}
}
