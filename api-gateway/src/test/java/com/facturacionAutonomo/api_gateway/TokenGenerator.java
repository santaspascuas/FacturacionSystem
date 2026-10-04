package com.facturacionAutonomo.api_gateway;

import java.io.InputStream;
import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.facturacionAutonomo.api_gateway.model.PruebaToken;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;



/*
 * Generamos el test para testear el gateway
 * */
public class TokenGenerator {
	
	private static final String ISSUER = "facturacion-auth";
	
	
	public static void main(String[] args) throws Exception {
		
		RSAPrivateKey key = loadPrivateKey();
		
		PruebaToken datosToken = new PruebaToken();
		datosToken.setIssuer("facturacion-auth");
		datosToken.setUserid("123-uuid-cliente");
		datosToken.setEmail("cliente@correo.com");
		datosToken.setRoles(List.of("USER"));
		datosToken.setExpiresAt(LocalDate.now().plusDays(1));
		
		String jwtToken = token(key, datosToken);
		
		System.out.println("\n=== TOKEN JWT GENERADO EXITOSAMENTE ===");
	    System.out.println(jwtToken);
		
	}

	private static RSAPrivateKey loadPrivateKey() {
		
		InputStream in = null;
		
		try {
			
			in = TokenGenerator.class.getResourceAsStream("/keys/private.pem");
			
			System.out.println("Estamos tomando el valor en stream");
			
			String limpieza = new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
			
			String keyCleaned = limpieza
			        .replace("-----BEGIN PRIVATE KEY-----", "")
			        .replace("-----END PRIVATE KEY-----", "")
			        .replaceAll("\\s+", ""); // Elimina espacios, enters y tabulaciones
			
			
			limpieza.lines().forEach(line ->{
				System.out.println("Línea leída: " + line);
			});
			
			byte[] decodedKey = java.util.Base64.getDecoder().decode(keyCleaned);
	        java.security.spec.PKCS8EncodedKeySpec keySpec = new java.security.spec.PKCS8EncodedKeySpec(decodedKey);
	        java.security.KeyFactory keyFactory = java.security.KeyFactory.getInstance("RSA");
	        
	        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
			
		}catch(Exception e) {
			
			throw new RuntimeException("Error al cargar la llave privada PEM", e);
		}finally {
			if(in!= null) {
				try {
					in.close();
				}catch(Exception ignored) {
					
				}
			}
		}
	}
	
	
	static String token(RSAPrivateKey key, PruebaToken prueba) {
		
		Date expirationDate = Date.from(
		        prueba.getExpiresAt()
		              .atStartOfDay(ZoneId.systemDefault())
		              .toInstant()
		    );
		
		JWTClaimsSet claims = new JWTClaimsSet.Builder()
				.issuer(prueba.getIssuer())
				.subject(prueba.getUserid())
				.claim("email", prueba.getEmail())
				.claim("role", prueba.getRoles())
				.issueTime(new Date())
				.expirationTime(expirationDate)
				.build();
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .type(JOSEObjectType.JWT)
                .build();

        SignedJWT jwt = new SignedJWT(header, claims);
        try {
			jwt.sign(new RSASSASigner(key));
		} catch (JOSEException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return jwt.serialize();	
	}
	
	
	
	
	
	
	

}
