package com.facturacionAutonomo.api_gateway.config;

import java.security.interfaces.RSAPublicKey;

import java.io.IOException;
import java.security.interfaces.RSAPublicKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.converter.RsaKeyConverters;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import reactor.core.publisher.Mono;



@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
	
	private final Resource publicKey;
	private final String issuer;
	private final String[] publicPaths;
	
	
	
	
	public SecurityConfig(
			@Value("${app.security.jwt.public-key}")Resource publicKey, 
			@Value("${app.security.jwt.issuer}")String issuer, 
			@Value("${app.security.jwt.public-paths}")String[] publicPaths) {
		super();
		this.publicKey = publicKey;
		this.issuer = issuer;
		this.publicPaths = publicPaths;
	}
	
	@Bean
	public ReactiveJwtDecoder jwtDecoder() throws IOException {
		
	      RSAPublicKey key = RsaKeyConverters.x509().convert((publicKey.getInputStream()));
	        NimbusReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder.withPublicKey(key).build();
	        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));
	        return decoder;
	}
	
	
	@Bean
    Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        JwtGrantedAuthoritiesConverter roles = new JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName("roles");
        roles.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(roles);
        return new ReactiveJwtAuthenticationConverterAdapter(converter);
    }
    
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .authorizeExchange(authorize -> authorize
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers(publicPaths).permitAll()         
                .pathMatchers("/admin/**").hasRole("ADMIN")
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor()))
            );
        System.out.println("RUTAS PUBLICAS: " + java.util.Arrays.toString(publicPaths));
        return http.build();
    }
	
	
	
    
	
	
	

}
