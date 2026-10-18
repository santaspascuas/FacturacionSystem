package com.facturacionAutonomo.auth_service.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	private static final String[] RUTAS_PUBLICAS = {
			"/api/auth/**",
			"/.well-known/jwks.json",
			"/actuator/health",
			"/actuator/health/**",
			"/error"
	};

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
					.requestMatchers(RUTAS_PUBLICAS).permitAll()
					.anyRequest().authenticated())
			.oauth2ResourceServer(oauth2 -> oauth2
					.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));

		return http.build();
	}

	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
		authorities.setAuthoritiesClaimName("roles");
		authorities.setAuthorityPrefix("ROLE_");

		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(authorities);
		return converter;
	}
}
