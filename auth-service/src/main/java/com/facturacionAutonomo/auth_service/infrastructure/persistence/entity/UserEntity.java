package com.facturacionAutonomo.auth_service.infrastructure.persistence.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;

import com.facturacionAutonomo.auth_service.domain.model.Role;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_generator")
    @SequenceGenerator(name = "user_id_generator", sequenceName = "user_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    @Email
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private UUID publicId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private  Role role;

    public void setRole(Role role) {
		this.role = role;
	}

	@Column(nullable = false)
    private boolean active = true;
    @Column(nullable = false)
    private boolean accountNonExpired;

    @Column(nullable = false)
    private int failedAttempts = 0;

    @Column(nullable = false)
    private boolean accountNonLocked;

    @Column(nullable = false)
    private boolean credentialsNonExpired = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lockTime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private TenantEntity tenant;

    @PrePersist
    protected void onCreate() {
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

	public UserEntity() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UUID getPublicId() {
		return publicId;
	}

	public void setPublicId(UUID publicId) {
		this.publicId = publicId;
	}

	public Role getRole() {
		return role;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	public int getFailedAttempts() {
		return failedAttempts;
	}

	public void setFailedAttempts(int failedAttempts) {
		this.failedAttempts = failedAttempts;
	}

	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
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

	public LocalDateTime getLockTime() {
		return lockTime;
	}

	public void setLockTime(LocalDateTime lockTime) {
		this.lockTime = lockTime;
	}

	public TenantEntity getTenant() {
		return tenant;
	}

	public void setTenant(TenantEntity tenant) {
		this.tenant = tenant;
	}
}
