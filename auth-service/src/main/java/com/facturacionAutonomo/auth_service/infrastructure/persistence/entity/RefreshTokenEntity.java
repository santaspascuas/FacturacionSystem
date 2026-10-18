package com.facturacionAutonomo.auth_service.infrastructure.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenEntity {
    @Id
    @Column(name = "refresh_token_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "refresh_token_id_generator")
    @SequenceGenerator(name = "refresh_token_id_generator", sequenceName = "refresh_token_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity userEntidad;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean revoked = false;

    @Version
    @Column(nullable = false)
    private Long version;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTokenHash() {
		return tokenHash;
	}

	public void setTokenHash(String tokenHash) {
		this.tokenHash = tokenHash;
	}

	public UserEntity getUserEntidad() {
		return userEntidad;
	}

	public void setUserEntidad(UserEntity userEntidad) {
		this.userEntidad = userEntidad;
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public boolean isRevoked() {
		return revoked;
	}

	public void setRevoked(boolean revoked) {
		this.revoked = revoked;
	}

	public Long getVersion() {
		return version;
	}

	@Override
	public String toString() {
		return "RefreshTokenEntity [id=" + id + ", expiresAt=" + expiresAt + ", createdAt=" + createdAt
				+ ", revoked=" + revoked + ", version=" + version + "]";
	}
}
