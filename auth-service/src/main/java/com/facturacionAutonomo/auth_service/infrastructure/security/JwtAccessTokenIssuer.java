package com.facturacionAutonomo.auth_service.infrastructure.security;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import com.facturacionAutonomo.auth_service.application.result.AccessToken;
import com.facturacionAutonomo.auth_service.application.port.out.TokenProviderPort;
import com.facturacionAutonomo.auth_service.domain.model.User;
import com.facturacionAutonomo.auth_service.infrastructure.config.JwtProperties;

@Component
public class JwtAccessTokenIssuer implements TokenProviderPort{
	private final JwtEncoder jwtEncoder;
	private final JwtProperties properties;
	private final Clock clock;

	private static final Logger log = LoggerFactory.getLogger(JwtAccessTokenIssuer.class);

	public JwtAccessTokenIssuer(JwtEncoder jwtEncoder, JwtProperties properties, Clock clock) {
		this.jwtEncoder = jwtEncoder;
		this.properties = properties;
		this.clock = clock;
	}

	@Override
	public AccessToken generarToken(User usuario) {
		log.info("[String generarToken] generatoken: {}", usuario.getEmail());

		Instant now = clock.instant();
		Duration ttl = properties.accessTokenTtl();

		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer(properties.issuer())
				.subject(usuario.getId().toString())
				.audience(List.of(properties.audience()))
				.issuedAt(now)
				.expiresAt(now.plus(ttl))
				.id(UUID.randomUUID().toString())
				.claim("tenant_id", usuario.getTenantId().toString())
				.claim("roles", List.of(usuario.getRole().name()))
				.build();

		JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256)
				.keyId(properties.keyId())
				.build();

		String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
		log.debug("[generarToken] emitido jti: {} sub: {} exp: {}", claims.getId(), claims.getSubject(), claims.getExpiresAt());

		return new AccessToken(token, ttl);
	}
}
