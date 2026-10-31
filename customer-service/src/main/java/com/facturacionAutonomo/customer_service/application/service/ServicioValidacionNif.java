package com.facturacionAutonomo.customer_service.application.service;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ServicioValidacionNif implements NifUtilInterface{

	private static final Logger log = LoggerFactory.getLogger(ServicioValidacionNif.class);

	private final char[] letrasPermitidas = {
		    'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D', 'X',
		    'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H', 'L', 'C', 'K', 'E'
		};
	private final char[] letrasControlCif = {'J', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'};

	// Letra inicial de un CIF según el tipo de entidad (A sociedad anónima, B limitada...).
	private static final String LETRAS_TIPO_CIF = "ABCDEFGHJNPQRSUVW";
	// Estos tipos llevan siempre letra de control; ABEH siempre dígito; el resto, cualquiera.
	private static final String CIF_CONTROL_LETRA = "PQRSWN";
	private static final String CIF_CONTROL_DIGITO = "ABEH";

	@Override
	public boolean confirmaNif(String dni) {
		if (dni == null || dni.isBlank()) {
			return false;
		}
		String limpiaMayus = dni.trim().toUpperCase(Locale.ROOT);
		if (limpiaMayus.length() != 9) {
			return false;
		}

		// El tipo de documento lo decide el PRIMER carácter, no el último:
		// un NIE (X1234567L) o un CIF (Q1234567D) también pueden terminar en letra.
		char primera = limpiaMayus.charAt(0);
		char letraFinal = limpiaMayus.charAt(8);
		log.debug("Analizamos nif. Primer caracter: {}, letra final: {}", primera, letraFinal);

		if (Character.isDigit(primera)) {
			return confirmaDni(limpiaMayus.substring(0, 8), letraFinal);
		}
		if (primera == 'X' || primera == 'Y' || primera == 'Z') {
			// NIE: X=0, Y=1, Z=2 y se valida como un DNI
			return confirmaDni("XYZ".indexOf(primera) + limpiaMayus.substring(1, 8), letraFinal);
		}
		if (primera == 'K' || primera == 'L' || primera == 'M') {
			// NIF especial K/L/M: misma letra de control que el DNI sobre sus 7 dígitos
			return confirmaDni(limpiaMayus.substring(1, 8), letraFinal);
		}
		log.debug("Empieza por letra de entidad. Activamos validacion de cif");
		return confirmaCif(limpiaMayus);
	}

	private boolean confirmaDni(String numeros, char letra) {
		if (!soloDigitos(numeros)) {
			return false;
		}
		int division = Integer.parseInt(numeros) % 23;
		char calculado = letrasPermitidas[division];
		return calculado == letra;
	}


	private boolean confirmaCif(String cif) {
		char tipo = cif.charAt(0);
		if (LETRAS_TIPO_CIF.indexOf(tipo) < 0) {
			return false;
		}

		// Solo los 7 dígitos centrales: el último carácter es el control y no entra en la suma.
		String numeros = cif.substring(1, 8);
		if (!soloDigitos(numeros)) {
			return false;
		}

		int sumapar =0;
		int sumaImpar = 0;
		int auxiliar = 0;

		for(int i =0; i< numeros.length();i++) {
			if((i + 1) % 2 == 0) {
				int digito = Character.getNumericValue(numeros.charAt(i));
				//Aqui van los pares
				sumapar+= digito;
			}else {
				auxiliar = Character.getNumericValue(numeros.charAt(i)) *2;

				//obtenemos 12
				if (auxiliar >= 10) {
	                sumaImpar += (auxiliar / 10) + (auxiliar % 10);
	            } else {
	                sumaImpar += auxiliar;
	            }
			}
		}

		log.debug("Suma par: {}, Suma impar: {}", sumapar, sumaImpar);

		int sumTotal = sumapar + sumaImpar;
		int unidades = sumTotal % 10;

		int digitoControlCalculado = (unidades == 0) ? 0 : (10 - unidades);
		char letraControlCalculada = letrasControlCif[digitoControlCalculado];
		char digitoControlComoChar = (char) ('0' + digitoControlCalculado);
		char control = cif.charAt(8);
		log.debug("Control recibido: {}, calculado: {} / {}", control, digitoControlComoChar, letraControlCalculada);

		if (CIF_CONTROL_LETRA.indexOf(tipo) >= 0) {
			return control == letraControlCalculada;
		}
		if (CIF_CONTROL_DIGITO.indexOf(tipo) >= 0) {
			return control == digitoControlComoChar;
		}
		return control == letraControlCalculada || control == digitoControlComoChar;
	}

	// Solo 0-9: Character.isDigit también aceptaría dígitos de otros alfabetos.
	private boolean soloDigitos(String valor) {
		for (int i = 0; i < valor.length(); i++) {
			char c = valor.charAt(i);
			if (c < '0' || c > '9') {
				return false;
			}
		}
		return true;
	}
}
