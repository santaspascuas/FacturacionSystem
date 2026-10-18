package com.facturacionAutonomo.auth_service.infrastructure.persistence.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "tenants")
public class TenantEntity {
	@Id
    @Column(name = "tenant_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tenant_id_generator")
    @SequenceGenerator(name = "tenant_id_generator", sequenceName = "tenant_id_seq", allocationSize = 1)
    private Long id;

	@Column(nullable = false, unique = true)
	private UUID publicId;

	@Column(nullable = false)
	private String name;

	@OneToMany(mappedBy = "tenant", fetch = FetchType.LAZY)
	private List<UserEntity> users = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

	public TenantEntity() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UUID getPublicId() {
		return publicId;
	}

	public void setPublicId(UUID publicId) {
		this.publicId = publicId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<UserEntity> getUsers() {
		return users;
	}

	public void setUsers(List<UserEntity> users) {
		this.users = users;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
