package com.facturacionAutonomo.product_service.infraestructure.persistence.entitys;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

/**
 * Un tenant tiene N usuarios TPV. El username es único dentro de cada tenant, no en toda la tabla.
 * La restricción real vive en la migración de Flyway; aquí solo se documenta (ddl-auto=validate).
 */
@Entity
@Table(name = "userstpv", uniqueConstraints = @UniqueConstraint(name = "uk_userstpv_tenant_username", columnNames = {
		"tenant_id", "username" }))
public class UserTpvEntity {
	
	@Id
	@Column(name = "usertpv_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usertpv_id_generator")
	@SequenceGenerator(name = "usertpv_id_generator", sequenceName = "usertpv_id_seq", allocationSize = 1)
	private Long usertpvId;
	
	@Column(name = "tenant_id", nullable = false, updatable = false)
	private UUID tenantId;
	
	@Column(nullable = false, length = 100)
	private String username;

	@Column(name = "hash_password", nullable = false, length = 100)
	private String hashPassword;

	@Column(name = "role_tpv", nullable = false, length = 100)
	private String roleTpv;
	
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;
	
	@Version
	private Long version;

	public UserTpvEntity() {
		super();
	}

	public Long getUsertpvId() {
		return usertpvId;
	}

	public void setUsertpvId(Long usertpvId) {
		this.usertpvId = usertpvId;
	}

	public UUID getTenantId() {
		return tenantId;
	}

	public void setTenantId(UUID tenantId) {
		this.tenantId = tenantId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getHashPassword() {
		return hashPassword;
	}

	public void setHashPassword(String hashPassword) {
		this.hashPassword = hashPassword;
	}

	public String getRoleTpv() {
		return roleTpv;
	}

	public void setRoleTpv(String roleTpv) {
		this.roleTpv = roleTpv;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
	
	
}
