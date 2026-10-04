package com.facturacionAutonomo.api_gateway.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class PruebaToken {
	
	private String issuer;
	private String email;
	private String userid;
	List<String> roles;
	private LocalDate expiresAt;
	
	public PruebaToken() {
		super();
	}
	public String getIssuer() {
		return issuer;
	}
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public List<String> getRoles() {
		return roles;
	}
	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public LocalDate getExpiresAt() {
		return expiresAt;
	}
	public void setExpiresAt(LocalDate expiresAt) {
		this.expiresAt = expiresAt;
	}
	
	
	
	
	
	
	
	
	
	
}
