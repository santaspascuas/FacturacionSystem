package com.facturacionAutonomo.auth_service.infrastructure.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.List;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.facturacionAutonomo.auth_service.application.config.RefreshTokenSettings;
import com.facturacionAutonomo.auth_service.infrastructure.config.JwtProperties;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {
	@Bean
	public RSAKey rsaKey(JwtProperties properties) {
		RSAPrivateKey privateKey = leerClavePrivada(properties);
		RSAPublicKey publicKey = derivarClavePublica(privateKey);

		return new RSAKey.Builder(publicKey)
				.privateKey(privateKey)
				.keyID(properties.keyId())
				.build();
	}

	@Bean
	public JWKSource<SecurityContext> jwkSource(RSAKey rsaKey) {
		return new ImmutableJWKSet<>(new JWKSet(rsaKey));
	}

	@Bean
	public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
		return new NimbusJwtEncoder(jwkSource);
	}

	@Bean
	public RefreshTokenSettings refreshTokenSettings(JwtProperties properties) {
		return new RefreshTokenSettings(properties.refreshTokenTtl());
	}

	@Bean
	public JwtDecoder jwtDecoder(RSAKey rsaKey, JwtProperties properties) throws Exception {
		NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey())
				.signatureAlgorithm(SignatureAlgorithm.RS256)
				.build();

		OAuth2TokenValidator<Jwt> audiencia = new JwtClaimValidator<List<String>>(JwtClaimNames.AUD,
				aud -> aud != null && aud.contains(properties.audience()));
		decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
				JwtValidators.createDefaultWithIssuer(properties.issuer()), audiencia));
		return decoder;
	}

	private static RSAPrivateKey leerClavePrivada(JwtProperties properties) {
		try (InputStream in = properties.privateKey().getInputStream()) {
			return RsaKeyConverters.pkcs8().convert(in);
		} catch (IOException | IllegalArgumentException ex) {
			throw new IllegalStateException("No se pudo leer la clave privada JWT (PEM PKCS#8) en: "
					+ properties.privateKey(), ex);
		}
	}

	private static RSAPublicKey derivarClavePublica(RSAPrivateKey privateKey) {
		if (!(privateKey instanceof RSAPrivateCrtKey crt)) {
			throw new IllegalStateException("La clave privada no incluye los datos para derivar la publica");
		}
		try {
			return (RSAPublicKey) KeyFactory.getInstance("RSA")
					.generatePublic(new RSAPublicKeySpec(crt.getModulus(), crt.getPublicExponent()));
		} catch (GeneralSecurityException ex) {
			throw new IllegalStateException("No se pudo derivar la clave publica JWT", ex);
		}
	}
}
